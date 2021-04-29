package com.mo.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * Created by mo on 2021/4/29
 */
@ApiModel(value = "商品锁定对象", description = "商品锁定请求对象")
@Data
public class LockProductRequest {

    @ApiModelProperty(value = "订单号")
    @JsonProperty("order_out_trade_no")
    private String orderOutTradeNo;

    @ApiModelProperty(value = "订单商品项目")
    @JsonProperty("order_item_list")
    private List<OrderItemRequest> orderItemList;
}
