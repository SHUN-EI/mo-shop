package com.mo.service;

import com.mo.model.MpOrderDO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mo.request.CreateOrderRequest;
import com.mo.utils.JsonData;

/**
 * @author mo
 * @since 2021-04-26
 */
public interface OrderService {

    JsonData createOrder(CreateOrderRequest request);

    JsonData queryOrderState(String outTradeNo);
}
