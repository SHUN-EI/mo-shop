package com.mo.model;

import lombok.Data;

/**
 * Created by mo on 2021/4/29
 */
@Data
public class ProductMessage {

    /**
     * 消息队列id
     */
    private Long messageId;

    /**
     * 订单号
     */
    private String outTradeNo;

    /**
     * 商品库存锁定任务id
     */
    private Long productTaskId;
}
