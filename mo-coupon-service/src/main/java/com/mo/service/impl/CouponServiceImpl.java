package com.mo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mo.enums.BizCodeEnum;
import com.mo.enums.CouponCategoryEnum;
import com.mo.enums.CouponPublishEnum;
import com.mo.enums.CouponStateEnum;
import com.mo.exception.BizException;
import com.mo.interceptor.LoginInterceptor;
import com.mo.mapper.MpCouponRecordMapper;
import com.mo.model.LoginUserDTO;
import com.mo.model.MpCouponDO;
import com.mo.mapper.MpCouponMapper;
import com.mo.model.MpCouponRecordDO;
import com.mo.request.NewUserCouponRequest;
import com.mo.service.CouponService;
import com.mo.utils.CommonUtil;
import com.mo.utils.JsonData;
import com.mo.vo.CouponVO;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <p>
 * 优惠券表 服务实现类
 * </p>
 *
 * @author mo
 * @since 2021-04-22
 */
@Service
@Slf4j
public class CouponServiceImpl implements CouponService {

    @Autowired
    private MpCouponMapper couponMapper;
    @Autowired
    private MpCouponRecordMapper couponRecordMapper;
    @Autowired
    private RedissonClient redissonClient;

    /**
     * 新用户注册领券优惠券
     * 用户微服务-新用户注册接口调用这个方法的时候，没有登录，所以没有token
     * 本地直接调用发放优惠券的方案，需要构造一个登录用户存储在threadlocal
     *
     * @param request
     * @return
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    @Override
    public JsonData initNewUserCoupon(NewUserCouponRequest request) {

        //构造一个登录用户存储在threadlocal
        LoginUserDTO loginUserDTO = new LoginUserDTO();
        loginUserDTO.setId(request.getUserId());
        loginUserDTO.setUserName(request.getUserName());
        LoginInterceptor.threadLocal.set(loginUserDTO);

        //查询新用户可领券的优惠券列表
        List<MpCouponDO> couponDOList = couponMapper.selectList(new QueryWrapper<MpCouponDO>()
                .eq("category", CouponCategoryEnum.NEW_USER.name()));

        //幂等操作，调用需要加锁，所以使用领券方法 addPromotionCoupon
        couponDOList.forEach(obj -> addCoupon(obj.getId(), CouponCategoryEnum.NEW_USER));

        return JsonData.buildSuccess();
    }

    /**
     * 领券
     *
     * @param couponId
     * @param couponCategoryEnum
     * @return
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    @Override
    public JsonData addCoupon(Long couponId, CouponCategoryEnum couponCategoryEnum) {

        LoginUserDTO loginUserDTO = LoginInterceptor.threadLocal.get();

        String lockKey = "lock:coupon:" + couponId;
        RLock lock = redissonClient.getLock(lockKey);
        //阻塞式等待，一个线程获取锁后，其他线程只能等待，和原生的方式循环调用不一样
        //锁的过期时间默认为30s，有 watch dog功能
        lock.lock();
        log.info("领券接口加锁成功:{}", Thread.currentThread().getId());

        //锁设置30s过期，没有watch dog功能，无法自动续期，无需unlock()
        //lock.lock(30,TimeUnit.SECONDS);

        //执行领券相关逻辑
        //判断获取优惠券是否存在
        MpCouponDO couponDO = couponMapper.selectOne(new QueryWrapper<MpCouponDO>()
                .eq("id", couponId)
                .eq("category", couponCategoryEnum.name()));

        //校验优惠券是否可以领取：时间、库存、超过限制张数
        couponCheck(couponDO, loginUserDTO.getId());

        //保存领券记录
        MpCouponRecordDO couponRecordDO = new MpCouponRecordDO();
        BeanUtils.copyProperties(couponDO, couponRecordDO);
        couponRecordDO.setCreateTime(new Date());
        couponRecordDO.setUpdateTime(new Date());
        couponRecordDO.setUseState(CouponStateEnum.NEW.name());
        couponRecordDO.setUserId(loginUserDTO.getId());
        couponRecordDO.setUserName(loginUserDTO.getUserName());
        couponRecordDO.setCouponId(couponId);
        couponRecordDO.setId(null);//copyProperties会把id拷贝，这里需要置null

        //扣减优惠券库存
        //高并发下扣减劵库存，采用乐观锁,当前stock做版本号,延伸多种防止超卖的问题,一次只能领取1张
        int rows = couponMapper.reduceStock(couponId);

        if (1 == rows) {
            //优惠券库存扣减成功才保存优惠券领劵记录
            couponRecordMapper.insert(couponRecordDO);
        } else {
            log.warn("领取优惠券失败:{},用户:{}", couponDO, loginUserDTO);
            throw new BizException(BizCodeEnum.COUPON_NO_STOCK);
        }

        lock.unlock();
        log.info("领券接口解锁成功");

        return JsonData.buildSuccess();
    }

    @Override
    public Map<String, Object> pageCouponActivity(int page, int size) {
        //第1页，每页10条
        Page<MpCouponDO> pageInfo = new Page<>(page, size);

        IPage<MpCouponDO> couponDOPage = couponMapper.selectPage(pageInfo, new QueryWrapper<MpCouponDO>()
                .eq("publish", CouponPublishEnum.PUBLISH)
                .eq("category", CouponCategoryEnum.PROMOTION)
                .orderByDesc("create_time"));


        HashMap<String, Object> pageMap = new HashMap<>(3);
        //总条数
        pageMap.put("total_record", couponDOPage.getTotal());
        //总页数
        pageMap.put("total_page", couponDOPage.getPages());

        //组装返回前端的对象
        List<CouponVO> couponVOList = couponDOPage.getRecords().stream()
                .map(obj -> {
                    CouponVO couponVO = new CouponVO();
                    BeanUtils.copyProperties(obj, couponVO);
                    return couponVO;
                }).collect(Collectors.toList());

        pageMap.put("current_data", couponVOList);

        return pageMap;
    }

    /**
     * 优惠券领取逻辑校验
     *
     * @param couponDO
     * @param userId
     */
    private void couponCheck(MpCouponDO couponDO, Long userId) {

        //优惠券是否存在
        if (null == couponDO) {
            throw new BizException(BizCodeEnum.COUPON_NO_EXITS);
        }

        //优惠券是否为发布状态
        if (!couponDO.getPublish().equals(CouponPublishEnum.PUBLISH.name())) {
            throw new BizException(BizCodeEnum.COUPON_GET_FAIL);
        }

        //库存是否足够
        if (couponDO.getStock() <= 0) {
            throw new BizException(BizCodeEnum.COUPON_NO_STOCK);
        }

        //是否在领取时间范围内
        Long currentTime = CommonUtil.getCurrentTimestamp();
        long startTime = couponDO.getStartTime().getTime();
        long endTime = couponDO.getEndTime().getTime();
        //未到优惠券开始有效时间 或 超过失效时间
        if (currentTime < startTime || currentTime > endTime) {
            throw new BizException(BizCodeEnum.COUPON_OUT_OF_TIME);
        }

        //
        int recordNum = couponRecordMapper.selectCount(new QueryWrapper<MpCouponRecordDO>()
                .eq("coupon_id", couponDO.getId())
                .eq("user_id", userId));

        if (recordNum >= couponDO.getUserLimit()) {
            throw new BizException(BizCodeEnum.COUPON_OUT_OF_LIMIT);
        }

    }
}
