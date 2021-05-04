package com.mo.constant;

/**
 * Created by mo on 2021/5/4
 */
public class TimeConstant {

    /**
     * 支付订单的有效时长，超过时间未支付则关闭订单
     * 订单超时时间，毫秒，默认30分钟
     */
    public static final long ORDER_PAY_TIMEOUT_MILLS = 30 * 60 * 1000;
}
