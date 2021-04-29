package com.mo.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by mo on 2021/4/29
 */
@ApiModel(value = "订单商品对象", description = "订单商品请求对象")
@Data
public class OrderItemRequest {

    @ApiModelProperty(value = "商品id")
    @JsonProperty("product_id")
    private Long productId;
    @ApiModelProperty(value = "购买数量")
    @JsonProperty("buy_num")
    private Integer buyNum;
}
