package com.mo.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * Created by mo on 2021/4/28
 */
@ApiModel(value = "优惠券锁定对象", description = "优惠券锁定请求对象")
@Data
public class LockCouponRecordRequest {

    @ApiModelProperty(value = "优惠券记录id列表",example = "[1,2,3]")
    @JsonProperty("lock_coupon_record_ids")
    private List<Long> lockCouponRecordIds;

    @ApiModelProperty(value = "订单号")
    @JsonProperty("order_out_trade_no")
    private String orderOutTradeNo;
}
