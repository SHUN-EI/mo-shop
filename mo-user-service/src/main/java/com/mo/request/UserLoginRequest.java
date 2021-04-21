package com.mo.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by mo on 2021/4/21
 */
@ApiModel(value = "用户登录对象", description = "用户登录请求对象")
@Data
public class UserLoginRequest {

    @ApiModelProperty(value = "邮箱",example = "295597613@qq.com")
    private String mail;
    @ApiModelProperty(value = "密码",example = "12345")
    private String password;
}
