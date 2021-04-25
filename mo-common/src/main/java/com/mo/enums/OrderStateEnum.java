package com.mo.enums;

/**
 * Created by mo on 2021/4/26
 * 订单状态
 */
public enum OrderStateEnum {

    /**
     * 未支付订单
     */
    NEW,
    /**
     * 已经支付订单
     */
    PAY,

    /**
     * 超时取消订单
     */
    CANCEL;
}
