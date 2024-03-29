package com.mo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

/**
 * Created by mo on 2021/4/21
 * 用户登录对象
 */
@Data
@Builder
public class LoginUserDTO {

    /**
     * 用户id
     */
    private Long id;
    /**
     * 用户名
     */
    private String userName;
    /**
     * 头像
     */
    @JsonProperty("head_img")
    private String headImg;
    /**
     * 邮箱
     */
    private String mail;

    public LoginUserDTO(){}

    public LoginUserDTO(Long id, String userName, String headImg, String mail) {

        this.id = id;
        this.userName = userName;
        this.headImg = headImg;
        this.mail = mail;
    }

}
