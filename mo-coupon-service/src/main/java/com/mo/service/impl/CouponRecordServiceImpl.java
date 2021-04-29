package com.mo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mo.config.RabbitMQConfig;
import com.mo.enums.BizCodeEnum;
import com.mo.enums.CouponStateEnum;
import com.mo.enums.LockStateEnum;
import com.mo.enums.OrderStateEnum;
import com.mo.exception.BizException;
import com.mo.feign.OrderFeignService;
import com.mo.interceptor.LoginInterceptor;
import com.mo.mapper.CouponTaskMapper;
import com.mo.model.CouponRecordMessage;
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
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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
    @Autowired
    private RabbitMQConfig rabbitMQConfig;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private OrderFeignService orderFeignService;


    /**
     * 解锁优惠券记录
     *
     * @param recordMessage
     * @return
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    @Override
    public boolean releaseCouponRecord(CouponRecordMessage recordMessage) {
        //查询coupon_task 优惠券库存锁定任务是否存在
        CouponTaskDO couponTaskDO = couponTaskMapper.selectOne(new QueryWrapper<CouponTaskDO>().eq("id", recordMessage.getCouponTaskId()));

        if (null == couponTaskDO) {
            log.warn("优惠券库存锁定任务不存在，消息体={}", recordMessage);
            return true;//消息已经被消费了
        }

        //lock状态才处理
        if (couponTaskDO.getLockState().equalsIgnoreCase(LockStateEnum.LOCK.name())) {

            //查询订单状态,远程调用order微服务查询订单状态接口
            JsonData jsonData = orderFeignService.queryOrderState(recordMessage.getOutTradeNo());
            //判断查询订单状态接口是否正常响应
            if (jsonData.getCode() == 0) {
                //判断订单状态
                String state = jsonData.getData().toString();

                //若订单状态为 NEW-新建未支付订单，则消息需要返回队列，重新投递
                //正常不会查到状态为NEW的订单，因为优惠券库存锁定的消息队列设置的延迟时间是比订单消息队列要长的
                //则订单服务那边应该会先查支付状态，根据支付状态修改订单状态为PAY或CANCEL
                if (OrderStateEnum.NEW.name().equalsIgnoreCase(state)) {
                    log.warn("订单状态为NEW,消息需要返回队列，重新投递:{}", recordMessage);
                    return false;//消息需要返回队列，重新投递
                }

                //若订单状态为PAY-已经支付订单,需要修改优惠券库存锁定任务的记录Task的状态为FINISH
                if (OrderStateEnum.PAY.name().equalsIgnoreCase(state)) {
                    couponTaskDO.setLockState(LockStateEnum.FINISH.name());
                    couponTaskMapper.update(couponTaskDO, new QueryWrapper<CouponTaskDO>().eq("id", recordMessage.getCouponTaskId()));
                    log.info("订单已经支付，修改优惠券库存锁定记录状态为FINISH:{}", recordMessage);
                    return true;
                }
            }

            //若订单不存在或订单状态为CANCEL-超时取消订单，确认并消费消息，修改coupon_task状态为CANCEL,恢复优惠券使用记录为NEW
            log.warn("订单不存在，或订单超时被取消，确认并消费消息，修改coupon_task状态为CANCEL,恢复优惠券使用记录为NEW,message:{}", recordMessage);
            //恢复优惠券记录的使用状态为NEW
            couponRecordMapper.updateState(couponTaskDO.getCouponRecordId(), CouponStateEnum.NEW.name());

            //修改优惠券库存锁定任务的锁定状态为 CANCEL
            couponTaskDO.setLockState(LockStateEnum.CANCEL.name());
            couponTaskMapper.update(couponTaskDO, new QueryWrapper<CouponTaskDO>().eq("id", recordMessage.getCouponTaskId()));

            return true;//消息已经被消费了
        } else {
            log.warn("优惠券库存锁定状态不是lock，state={},消息体={}", couponTaskDO.getLockState(), recordMessage);
            return true;//消息已经被消费了
        }

    }

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
            //发送延迟消息
            couponTaskDOList.forEach(obj -> {
                CouponRecordMessage message = new CouponRecordMessage();
                message.setOutTradeNo(orderOutTradeNo);
                message.setCouponTaskId(obj.getId());
                rabbitTemplate.convertAndSend(rabbitMQConfig.getCouponEventExchange(),
                        rabbitMQConfig.getCouponReleaseDelayRoutingKey(),
                        message);

                log.info("优惠券锁定消息发送成功:{}", message.toString());
            });

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
