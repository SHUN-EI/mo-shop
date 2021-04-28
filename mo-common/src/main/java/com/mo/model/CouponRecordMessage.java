package com.mo.model;

import lombok.Data;

/**
 * Created by mo on 2021/4/28
 */
@Data
public class CouponRecordMessage {

    /**
     * 消息队列id
     */
    private Long messageId;

    /**
     * 订单号
     */
    private String outTradeNo;

    /**
     * 优惠券库存锁定任务id
     */
    private Long couponTaskId;
}
