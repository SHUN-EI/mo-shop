package com.mo.service;

import com.mo.enums.OrderPayTypeEnum;
import com.mo.model.MpOrderDO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mo.model.OrderMessage;
import com.mo.request.CreateOrderRequest;
import com.mo.utils.JsonData;

import java.util.Map;

/**
 * @author mo
 * @since 2021-04-26
 */
public interface OrderService {

    JsonData createOrder(CreateOrderRequest request);

    JsonData queryOrderState(String outTradeNo);

    boolean closeOrder(OrderMessage orderMessage);

    JsonData handlerOrderCallbackMsg(OrderPayTypeEnum payType, Map<String, String> paramsMap);
}
