package com.mo.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by mo on 2021/5/5
 */
@Data
public class OrderVO {

    /**
     * 订单id
     */
    private Long id;

    /**
     * 订单唯一标识
     */
    @JsonProperty("out_trade_no")
    private String outTradeNo;

    /**
     * NEW 未支付订单,PAY已经支付订单,CANCEL超时取消订单
     */
    private String state;

    /**
     * 订单总金额
     */
    @JsonProperty("total_amount")
    private BigDecimal totalAmount;

    /**
     * 订单实际支付价格
     */
    @JsonProperty("actual_amount")
    private BigDecimal actualAmount;

    /**
     * 支付类型，微信-银行-支付宝
     */
    @JsonProperty("pay_type")
    private String payType;

    /**
     * 头像
     */
    @JsonProperty("head_img")
    private String headImg;

    /**
     * 用户id
     */
    @JsonProperty("user_id")
    private Long userId;

    /**
     * 昵称,用户名
     */
    @JsonProperty("user_name")
    private String userName;

    /**
     * 0表示未删除，1表示已经删除
     */
    @JsonProperty("is_deleted")
    private Integer isDeleted;

    /**
     * 订单类型 DAILY普通单，PROMOTION促销订单
     */
    @JsonProperty("order_type")
    private String orderType;

    /**
     * 收货地址 json存储
     */
    @JsonProperty("receiver_address")
    private String receiverAddress;

    /**
     * 订单创建时间
     */
    @JsonProperty("create_time")
    private Date createTime;

    /**
     * 订单详情
     */
    private List<CartItemVO> cartItemVOList;
}
