package com.mo.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * Created by mo on 2021/5/5
 */
@ApiModel(value = "订单二次支付对象", description = "订单二次支付请求对象")
@Data
public class RepayOrderRequest {

    /**
     * 订单号
     */
    @JsonProperty("out_trade_no")
    private String outTradeNo;
    /**
     * 支付类型- 微信-银行卡-支付宝
     */
    @JsonProperty("pay_type")
    private String payType;
    /**
     * 端类型
     */
    @JsonProperty("client_type")
    private String clientType;
}
