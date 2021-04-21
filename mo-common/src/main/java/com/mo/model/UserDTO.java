package com.mo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Created by mo on 2021/4/21
 */
@Data
public class UserDTO {

    /**
     * 用户id
     */
    private Long id;
    /**
     * 用户名
     */
    private String userName;
    /**
     *头像
     */
    @JsonProperty("head_img")
    private String headImg;
    /**
     * 邮箱
     */
    private String mail;

}
