package com.mo.service.impl;

import com.mo.request.CreateOrderRequest;
import com.mo.service.OrderService;
import com.mo.utils.JsonData;
import org.springframework.stereotype.Service;

/**
 * @author mo
 * @since 2021-04-26
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Override
    public JsonData createOrder(CreateOrderRequest request) {

        //防重提交

        //用户微服务-确认收货地址，防止越权

        //商品微服务-获取最新购物车商品项目和价格

        //订单验价，后端需要计算校验订单价格，不能单以前端为准
        //优惠券微服务-获取优惠券
        //验证价格

        //锁定优惠券

        //锁定商品库存

        //创建订单对象

        //创建订单详情对象

        //发送延迟消息-用于自动关单

        //创建支付信息-对接第三方支付

        return null;
    }
}
