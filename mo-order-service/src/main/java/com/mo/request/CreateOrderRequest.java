package com.mo.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by mo on 2021/4/26
 */
@ApiModel(value = "创建订单对象", description = "创建订单请求对象")
@Data
public class CreateOrderRequest {

    /**
     * 购物车使用的优惠券
     * 若传空或者小于0，则不使用优惠券
     */
    @ApiModelProperty(value = "优惠券id", example = "1")
    @JsonProperty("coupon_record_id")
    private Long couponRecordId;

    /**
     * 最终购买的商品列表
     * 传递商品id集合，商品的购买数量可从购物车中获取
     */
    @ApiModelProperty(value = "商品id列表", example = "[1,2,3]")
    @JsonProperty("product_ids")
    private List<Long> productIds;

    /**
     * 支付方式
     */
    @ApiModelProperty(value = "支付方式", example = "微信")
    @JsonProperty("pay_type")
    private String payType;

    /**
     * 客户端类型
     */
    @ApiModelProperty(value = "客户端", example = "原生应用")
    @JsonProperty("client_type")
    private String clientType;

    /**
     * 收货地址
     */
    @ApiModelProperty(value = "收货地址", example = "1")
    @JsonProperty("address_id")
    private Long addressId;

    /**
     * 总价格，前端传递，后端需要验价
     */
    @ApiModelProperty(value = "总价格", example = "180")
    @JsonProperty("total_amount")
    private BigDecimal totalAmount;

    /**
     * 实际支付价格
     * 若使用了优惠券，则是减去优惠券之后的价格，若没有使用优惠券，则与totalAmount一样
     */
    @ApiModelProperty(value = "实际支付价格", example = "160")
    @JsonProperty("actual_amount")
    private BigDecimal actualAmount;

    /**
     * 防止重复提交的令牌
     */
    @ApiModelProperty(value = "防止重复提交的令牌", example = "")
    private String token;
}
