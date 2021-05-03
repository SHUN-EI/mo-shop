package com.mo.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mo.config.RabbitMQConfig;
import com.mo.enums.*;
import com.mo.exception.BizException;
import com.mo.feign.CartFeignService;
import com.mo.feign.CouponFeignService;
import com.mo.feign.ProductFeignService;
import com.mo.feign.UserFeignService;
import com.mo.interceptor.LoginInterceptor;
import com.mo.mapper.MpOrderDetailMapper;
import com.mo.mapper.MpOrderMapper;
import com.mo.model.LoginUserDTO;
import com.mo.model.MpOrderDO;
import com.mo.model.MpOrderDetailDO;
import com.mo.model.OrderMessage;
import com.mo.request.*;
import com.mo.service.OrderService;
import com.mo.utils.CommonUtil;
import com.mo.utils.JsonData;
import com.mo.utils.OrderCodeGenerateUtil;
import com.mo.vo.AddressVO;
import com.mo.vo.CartItemVO;
import com.mo.vo.CouponRecordVO;
import com.mysql.cj.log.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author mo
 * @since 2021-04-26
 */
@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Autowired
    private MpOrderMapper orderMapper;
    @Autowired
    private OrderCodeGenerateUtil orderCodeGenerateUtil;
    @Autowired
    private UserFeignService userFeignService;
    @Autowired
    private CartFeignService cartFeignService;
    @Autowired
    private CouponFeignService couponFeignService;
    @Autowired
    private ProductFeignService productFeignService;
    @Autowired
    private MpOrderDetailMapper orderDetailMapper;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private RabbitMQConfig rabbitMQConfig;

    @Override
    public JsonData createOrder(CreateOrderRequest request) {

        LoginUserDTO loginUserDTO = LoginInterceptor.threadLocal.get();
        //生成订单号
        String outTradeNo = orderCodeGenerateUtil.generateOrderCode(OrderCodeEnum.XD);
        //防重提交

        //用户微服务-确认收货地址，防止越权
        AddressVO addressVO = getUserAddress(request.getAddressId());
        log.info("收货地址信息:{}", addressVO);

        //商品微服务-获取最新购物车商品项目和价格
        List<Long> productIds = request.getProductIds();
        JsonData cartItemData = cartFeignService.confirmOrderCartItems(productIds);
        List<CartItemVO> cartItemVOList = cartItemData.getData(new TypeReference<>() {
        });
        log.info("获取对应订单购物车里面的商品信息:{}", cartItemVOList);
        if (null == cartItemVOList) {
            throw new BizException(BizCodeEnum.ORDER_CONFIRM_CART_ITEM_NOT_EXIST);
        }

        //订单验证价格，后端需要计算校验订单价格，不能单以前端为准
        checkPrice(cartItemVOList, request);

        //锁定优惠券
        lockCouponRecords(request, outTradeNo);

        //锁定商品库存
        lockProducts(cartItemVOList, outTradeNo);

        //锁定购物车商品项目,用于订单创建失败，恢复购物车商品项目
        lockCartItems(loginUserDTO.getId(), cartItemVOList, outTradeNo);

        //创建订单对象
        MpOrderDO orderDO = saveOrder(request, loginUserDTO, outTradeNo, addressVO);

        //创建订单详情对象
        saveOrderDetail(orderDO.getId(), outTradeNo, cartItemVOList);

        //发送延迟消息-用于自动关单
        sendOrderCloseMessage(outTradeNo);

        //创建支付信息-对接第三方支付

        return null;
    }

    /**
     * 发送延迟消息-用于自动关单
     *
     * @param outTradeNo
     */
    private void sendOrderCloseMessage(String outTradeNo) {

        OrderMessage message = new OrderMessage();
        message.setOutTradeNo(outTradeNo);
        rabbitTemplate.convertAndSend(rabbitMQConfig.getOrderEventExchange(),
                rabbitMQConfig.getOrderCloseDelayRoutingKey(), message);
    }

    /**
     * 创建订单详情
     *
     * @param orderId
     * @param outTradeNo
     * @param cartItemVOList
     */
    private void saveOrderDetail(Long orderId, String outTradeNo, List<CartItemVO> cartItemVOList) {

        List<MpOrderDetailDO> orderDetailDOList = cartItemVOList.stream().map(obj -> {
            MpOrderDetailDO orderDetailDO = MpOrderDetailDO.builder()
                    .orderId(orderId)
                    .outTradeNo(outTradeNo)
                    .productId(obj.getProductId())
                    .buyNum(obj.getBuyNum())
                    .productImg(obj.getProductImg())
                    .productName(obj.getProductTitle())
                    .amount(obj.getAmount())
                    .totalAmount(obj.getTotalAmount())
                    .createTime(new Date())
                    .updateTime(new Date())
                    .build();
            return orderDetailDO;
        }).collect(Collectors.toList());

        orderDetailMapper.insertBatch(orderDetailDOList);

    }

    /**
     * 创建订单
     *
     * @param request
     * @param loginUserDTO
     * @param outTradeNo
     * @param addressVO
     * @return
     */
    private MpOrderDO saveOrder(CreateOrderRequest request, LoginUserDTO loginUserDTO, String outTradeNo, AddressVO addressVO) {
        MpOrderDO orderDO = MpOrderDO.builder()
                .userId(loginUserDTO.getId())
                .headImg(loginUserDTO.getHeadImg())
                .userName(loginUserDTO.getUserName())
                .outTradeNo(outTradeNo)
                .createTime(new Date())
                .updateTime(new Date())
                .isDeleted(0)
                .orderType(OrderTypeEnum.DAILY.name())
                .actualAmount(request.getActualAmount())//实际支付价格
                .totalAmount(request.getTotalAmount())
                .state(OrderStateEnum.NEW.name())//订单状态
                .payType(OrderPayTypeEnum.valueOf(request.getPayType()).name())
                .receiverAddress(JSON.toJSONString(addressVO))
                .build();

        orderMapper.insert(orderDO);

        return orderDO;
    }

    /**
     * 锁定购物车商品项目
     *
     * @param id
     * @param cartItemVOList
     * @param outTradeNo
     */
    private void lockCartItems(Long id, List<CartItemVO> cartItemVOList, String outTradeNo) {
        List<OrderItemRequest> orderItemRequestList = cartItemVOList.stream().map(obj -> {
            OrderItemRequest request = new OrderItemRequest();
            request.setProductId(obj.getProductId());
            request.setBuyNum(obj.getBuyNum());
            return request;
        }).collect(Collectors.toList());

        LockCartItemsRequest lockCartItemsRequest = new LockCartItemsRequest();
        lockCartItemsRequest.setUserId(id);
        lockCartItemsRequest.setOrderOutTradeNo(outTradeNo);
        lockCartItemsRequest.setOrderItemList(orderItemRequestList);
        JsonData jsonData = cartFeignService.lockCartItems(lockCartItemsRequest);
        if (jsonData.getCode() != 0) {
            log.error("购物车商品项目锁定失败:{}", lockCartItemsRequest);
            throw new BizException(BizCodeEnum.CART_ITEM_LOCK_FAIL);
        }
    }

    /**
     * 锁定商品库存
     *
     * @param cartItemVOList
     * @param outTradeNo
     */
    private void lockProducts(List<CartItemVO> cartItemVOList, String outTradeNo) {
        List<OrderItemRequest> orderItemRequestList = cartItemVOList.stream().map(obj -> {
            OrderItemRequest request = new OrderItemRequest();
            request.setBuyNum(obj.getBuyNum());
            request.setProductId(obj.getProductId());
            return request;
        }).collect(Collectors.toList());

        LockProductRequest lockProductRequest = new LockProductRequest();
        lockProductRequest.setOrderOutTradeNo(outTradeNo);
        lockProductRequest.setOrderItemList(orderItemRequestList);
        JsonData jsonData = productFeignService.lockProducts(lockProductRequest);
        if (jsonData.getCode() != 0) {
            log.error("锁定商品库存失败:{}", lockProductRequest);
            throw new BizException(BizCodeEnum.ORDER_CONFIRM_LOCK_PRODUCT_FAIL);
        }

    }


    /**
     * 锁定优惠券
     *
     * @param request
     * @param outTradeNo
     */
    private void lockCouponRecords(CreateOrderRequest request, String outTradeNo) {

        List<Long> lockCouponRecordIds = new ArrayList<>();
        //订单有使用优惠券
        if (request.getCouponRecordId() > 0) {
            lockCouponRecordIds.add(request.getCouponRecordId());
            LockCouponRecordRequest lockCouponRecordRequest = new LockCouponRecordRequest();
            lockCouponRecordRequest.setOrderOutTradeNo(outTradeNo);
            lockCouponRecordRequest.setLockCouponRecordIds(lockCouponRecordIds);

            //发送优惠券锁定请求
            JsonData jsonData = couponFeignService.lockCouponRecords(lockCouponRecordRequest);
            if (jsonData.getCode() != 0) {
                log.error("锁定优惠券失败:{}", lockCouponRecordRequest);
                throw new BizException(BizCodeEnum.COUPON_RECORD_LOCK_FAIL);
            }
        }

    }

    /**
     * 订单验证价格
     *
     * @param cartItemVOList
     * @param request
     */
    private void checkPrice(List<CartItemVO> cartItemVOList, CreateOrderRequest request) {

        BigDecimal finalAmount = new BigDecimal("0");
        //统计全部商品的价格
        if (null != cartItemVOList) {
            for (CartItemVO cartItemVO : cartItemVOList) {
                BigDecimal itemTotalAmount = cartItemVO.getTotalAmount();
                finalAmount = finalAmount.add(itemTotalAmount);
            }
        }

        //获取优惠券(判断是否满足优惠券的使用条件)
        CouponRecordVO cartCouponRecord = getCartCouponRecord(request.getCouponRecordId());

        //计算购物车价格，是否满足优惠券的满减条件
        if (cartCouponRecord != null) {

            //计算是否满足满减(优惠券里面有个属性是满多少才可以使用)
            if (finalAmount.compareTo(cartCouponRecord.getConditionPrice()) < 0) {
                throw new BizException(BizCodeEnum.ORDER_CONFIRM_COUPON_FAIL);
            }
            //当优惠券的价格，比商品的总价格还大，最终价格为0
            if (cartCouponRecord.getPrice().compareTo(finalAmount) > 0) {
                finalAmount = BigDecimal.ZERO;
            } else {

                //总价格-优惠券价格=最终的价格
                finalAmount = finalAmount.subtract(cartCouponRecord.getPrice());
            }
        }

        //后台计算的总价格与前端传过来的总价格比较，若不相等，则验价失败，不能创建订单
        if (finalAmount.compareTo(request.getTotalAmount()) != 0) {
            log.error("订单验价失败:{}", request);
            throw new BizException(BizCodeEnum.ORDER_CONFIRM_PRICE_FAIL);
        }

    }

    /**
     * 获取优惠券
     *
     * @param couponRecordId
     * @return
     */
    private CouponRecordVO getCartCouponRecord(Long couponRecordId) {

        if (couponRecordId == null || couponRecordId < 0) {
            return null;
        }

        JsonData couponRecordData = couponFeignService.findUserCouponRecord(couponRecordId);
        if (couponRecordData.getCode() != 0) {
            throw new BizException(BizCodeEnum.ORDER_CONFIRM_COUPON_FAIL);
        }

        CouponRecordVO couponRecordVO = couponRecordData.getData(new TypeReference<>() {
        });

        if (couponRecordData.getCode() == 0) {
            if (!couponAvailable(couponRecordVO)) {
                log.error("优惠券使用失败");
                throw new BizException(BizCodeEnum.COUPON_UNAVAILABLE);
            }
            return couponRecordVO;
        }

        return null;
    }

    /**
     * 判断优惠券是否可用
     *
     * @param couponRecordVO
     * @return
     */
    private boolean couponAvailable(CouponRecordVO couponRecordVO) {

        if (couponRecordVO.getUseState().equalsIgnoreCase(CouponStateEnum.NEW.name())) {
            long currentTimestamp = CommonUtil.getCurrentTimestamp();
            long startTime = couponRecordVO.getStartTime().getTime();
            long endTime = couponRecordVO.getEndTime().getTime();

            if (currentTimestamp >= startTime && currentTimestamp <= endTime) {
                return true;
            }
        }

        return false;
    }


    /**
     * 查询订单状态
     *
     * @param outTradeNo
     * @return
     */
    @Override
    public JsonData queryOrderState(String outTradeNo) {

        MpOrderDO orderDO = orderMapper.selectOne(new QueryWrapper<MpOrderDO>().eq("out_trade_no", outTradeNo));

        if (null == orderDO) {
            return null;
        } else {
            return JsonData.buildSuccess(orderDO);
        }
    }

    /**
     * 获取用户收货地址
     *
     * @param addressId
     * @return
     */
    private AddressVO getUserAddress(Long addressId) {
        JsonData data = userFeignService.detail(addressId);

        if (data.getCode() != 0) {
            log.error("获取收货地址失败,msg:{}", data);
            throw new BizException(BizCodeEnum.ADDRESS_NOT_EXIST);
        }
        AddressVO addressVO = data.getData(new TypeReference<>() {
        });

        return addressVO;
    }
}
