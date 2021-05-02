package com.mo.model;

import lombok.Data;

/**
 * Created by mo on 2021/5/2
 */
@Data
public class CartMessage {

    /**
     * 消息队列id
     */
    private Long messageId;

    /**
     * 订单号
     */
    private String outTradeNo;

    /**
     * 购物车商品项目锁定任务id
     */
    private Long cartTaskId;
}
