package com.mo.constant;

/**
 * Created by mo on 2021/4/20
 */
public class CacheKey {

    /**
     * 注册验证码，第一个%s是类型，第二个%s是接收号码
     */
    public static final String CHECK_CODE_KEY = "code:%s:%s";

    /**
     * 购物车 hahs 结构，%s是用户唯一标识 user_id
     */
    public static final String CART_KEY = "cart:%s";
}
