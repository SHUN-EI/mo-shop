package com.mo.model;

import lombok.Data;

/**
 * Created by mo on 2021/5/3
 */
@Data
public class OrderMessage {

    /**
     * 消息队列id
     */
    private Long messageId;

    /**
     * 订单号
     */
    private String outTradeNo;

}
