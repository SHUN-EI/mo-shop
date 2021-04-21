package com.mo.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by mo on 2021/4/21
 * 用户注册请求
 */
@ApiModel(value = "用户注册对象", description = "用户注册请求对象")
@Data
public class UserRegisterRequest {

    @ApiModelProperty(value = "用户名", example = "老王")
    private String userName;
    @ApiModelProperty(value = "密码", example = "12345")
    private String password;
    @ApiModelProperty(value = "头像")
    @JsonProperty("head_img")
    private String headImg;
    @ApiModelProperty(value = "用户个性签名",example ="人生需要动态规划，学习需要贪心算法" )
    private String slogan;
    @ApiModelProperty(value = "0-女，1-男",example = "1")
    private  Integer sex;
    @ApiModelProperty(value = "邮箱",example = "295597613@qq.com")
    private String mail;
    @ApiModelProperty(value = "验证码",example = "123123")
    private String code;


}
