package com.mo.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by mo on 2021/4/25
 */
@ApiModel(value = "购物车商品对象", description = "购物车商品请求对象")
@Data
public class CartItemRequest {

    @ApiModelProperty(value = "商品id", example = "1")
    @JsonProperty("product_id")
    private Long productId;
    @ApiModelProperty(value = "商品购买数量", example = "2")
    @JsonProperty("buy_num")
    private Integer buyNum;
}
