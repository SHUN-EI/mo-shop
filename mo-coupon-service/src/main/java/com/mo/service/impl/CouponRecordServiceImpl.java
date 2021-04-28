package com.mo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mo.enums.BizCodeEnum;
import com.mo.enums.CouponStateEnum;
import com.mo.enums.LockStateEnum;
import com.mo.exception.BizException;
import com.mo.interceptor.LoginInterceptor;
import com.mo.mapper.CouponTaskMapper;
import com.mo.model.CouponTaskDO;
import com.mo.model.LoginUserDTO;
import com.mo.model.MpCouponRecordDO;
import com.mo.mapper.MpCouponRecordMapper;
import com.mo.request.LockCouponRecordRequest;
import com.mo.service.CouponRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mo.utils.JsonData;
import com.mo.vo.CouponRecordVO;
import com.mo.vo.CouponVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author mo
 * @since 2021-04-22
 */
@Service
@Slf4j
public class CouponRecordServiceImpl implements CouponRecordService {

    @Autowired
    private MpCouponRecordMapper couponRecordMapper;
    @Autowired
    private CouponTaskMapper couponTaskMapper;


    /**
     * 锁定优惠券
     *
     * @param request
     * @return
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    @Override
    public JsonData lockCouponRecords(LockCouponRecordRequest request) {
        LoginUserDTO loginUserDTO = LoginInterceptor.threadLocal.get();

        String orderOutTradeNo = request.getOrderOutTradeNo();
        List<Long> lockCouponRecordIds = request.getLockCouponRecordIds();

        //修改优惠券记录的使用状态为USED，表示优惠券已被使用,优惠券记录锁定
        int updateRows = couponRecordMapper.lockUseStateBatch(loginUserDTO.getId(), CouponStateEnum.USED.name(),
                CouponStateEnum.NEW.name(), lockCouponRecordIds);

        List<CouponTaskDO> couponTaskDOList = lockCouponRecordIds.stream().map(obj -> {
            CouponTaskDO couponTaskDO = new CouponTaskDO();
            couponTaskDO.setOutTradeNo(orderOutTradeNo);
            couponTaskDO.setCouponRecordId(obj);
            couponTaskDO.setCreateTime(new Date());
            couponTaskDO.setUpdateTime(new Date());
            couponTaskDO.setLockState(LockStateEnum.LOCK.name());

            return couponTaskDO;
        }).collect(Collectors.toList());

        //优惠券库存锁定任务表中 插入记录
        int insertRows = couponTaskMapper.insertBatch(couponTaskDOList);

        log.info("优惠券记录锁定 updateRows={}", updateRows);
        log.info("新增优惠券记录task insertRows={}", insertRows);

        if (lockCouponRecordIds.size() == insertRows && insertRows == updateRows) {
            //TODO 发送延迟消息

            return JsonData.buildSuccess();
        } else {
            throw new BizException(BizCodeEnum.COUPON_RECORD_LOCK_FAIL);
        }
    }

    /**
     * 查询优惠券记录信息
     * 水平权限攻击：也叫作访问控制攻击,Web应用程序接收到用户请求，修改某条数据时，没有判断数据的所属人，
     * 或者在判断数据所属人时从用户提交的表单参数中获取了userid。
     * 导致攻击者可以自行修改userid修改不属于自己的数据
     *
     * @param recordId
     * @return
     */
    @Override
    public CouponRecordVO findById(Long recordId) {

        LoginUserDTO loginUserDTO = LoginInterceptor.threadLocal.get();

        MpCouponRecordDO couponRecordDO = couponRecordMapper.selectOne(new QueryWrapper<MpCouponRecordDO>()
                .eq("id", recordId)
                .eq("user_id", loginUserDTO.getId()));

        if (null == couponRecordDO)
            return null;

        CouponRecordVO couponRecordVO = new CouponRecordVO();
        BeanUtils.copyProperties(couponRecordDO, couponRecordVO);

        return couponRecordVO;
    }

    @Override
    public Map<String, Object> pageCouponRecord(int page, int size) {

        LoginUserDTO loginUserDTO = LoginInterceptor.threadLocal.get();
        //第1页，每页10条
        Page<MpCouponRecordDO> pageInfo = new Page<>(page, size);

        IPage<MpCouponRecordDO> couponRecordDOPage = couponRecordMapper.selectPage(pageInfo, new QueryWrapper<MpCouponRecordDO>()
                .eq("user_id", loginUserDTO.getId())
                .orderByDesc("create_time"));

        Map<String, Object> pageMap = new HashMap<>(3);
        //总条数
        pageMap.put("total_record", couponRecordDOPage.getTotal());
        //总页数
        pageMap.put("total_page", couponRecordDOPage.getPages());

        //组装返回前端的对象
        List<CouponRecordVO> couponRecordVOList = couponRecordDOPage.getRecords().stream()
                .map(obj -> {
                    CouponRecordVO couponRecordVO = new CouponRecordVO();
                    BeanUtils.copyProperties(obj, couponRecordVO);
                    return couponRecordVO;
                }).collect(Collectors.toList());

        pageMap.put("current_data", couponRecordVOList);

        return pageMap;
    }
}
