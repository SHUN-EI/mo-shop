package com.mo.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by mo on 2021/4/25
 */
@ApiModel(value = "新用户注册发放优惠券对象", description = "新用户注册发放优惠券请求对象")
@Data
public class NewUserCouponRequest {

    @ApiModelProperty(value = "用户id", example = "1")
    @JsonProperty("user_id")
    private Long userId;

    @ApiModelProperty(value = "用户名", example = "马超")
    @JsonProperty("user_name")
    private String userName;

}
