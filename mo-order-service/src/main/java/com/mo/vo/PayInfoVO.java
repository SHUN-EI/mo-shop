package com.mo.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Created by mo on 2021/5/3
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PayInfoVO {

    /**
     * 订单号
     */
    @JsonProperty("out_trade_no")
    private String outTradeNo;

    /**
     * 订单总金额
     */
    @JsonProperty("total_amount")
    private BigDecimal totalAmount;

    /**
     * 支付类型 微信-支付宝-银行-其他
     */
    @JsonProperty("pay_type")
    private String payType;

    /**
     * 端类型 APP/H5/PC
     */
    @JsonProperty("client_type")
    private String clientType;

    /**
     * 标题
     */
    private String title;

    /**
     * 描述
     */
    private String description;

    /**
     * 订单支付超时时间，毫秒
     */
    @JsonProperty("order_pay_timeoutMills")
    private Long orderPayTimeoutMills;
}
