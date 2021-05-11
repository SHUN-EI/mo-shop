package com.mo.constant;

/**
 * Created by mo on 2021/5/4
 */
public class TimeConstant {

    /**
     * 支付订单的有效时长，超过时间未支付则关闭订单
     * 订单超时时间，毫秒，默认30分钟  30 * 60 * 1000
     * 为了方便测试，这里改为5分钟
     */
    public static final long ORDER_PAY_TIMEOUT_MILLS = 5 * 60 * 1000;

    public static final String YYYYMMDDHHMMSS_PREFIX = "yyyy-MM-dd HH:mm:ss";

    public static final String YYYYMMDD_PREFIX = "yyyy-MM-dd";

    public static final String HHMMSS_PREFIX = "HH:mm:ss";
}
