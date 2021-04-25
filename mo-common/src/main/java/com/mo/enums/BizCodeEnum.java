package com.mo.enums;

import lombok.Getter;

/**
 * Created by mo on 2021/4/18
 * 枚举类，统一状态码和错误信息
 *
 * @Description 状态码定义约束，共6位数，前3位代表服务，后3位代表接口
 * 比如 商品服务210,购物车是220、用户服务230，403代表权限
 */
public enum BizCodeEnum {

    /**
     * 通用操作码
     */
    OPS_REPEAT(110001, "重复操作"),

    /**
     * 验证码
     */
    CODE_TO_ERROR(240001, "接收号码不合规"),
    CODE_LIMITED(240002, "验证码发送过快,请稍后再试"),
    CODE_ERROR(240003, "验证码错误"),
    CODE_CAPTCHA_ERROR(240101, "图形验证码错误"),

    /**
     * 账号
     */
    ACCOUNT_REPEAT(250001, "账号已经存在"),
    ACCOUNT_UNREGISTER(250002, "账号不存在"),
    ACCOUNT_PWD_ERROR(250003, "账号或者密码错误"),
    ACCOUNT_UNLOGIN(250004, "账号未登录"),

    /**
     * 商品
     */
    PRODUCT_NOT_EXISTS(260001,"商品不存在"),

    /**
     * 优惠券
     */
    COUPON_CONDITION_ERROR(270001,"优惠券条件错误"),
    COUPON_UNAVAILABLE(270002,"没有可用的优惠券"),
    COUPON_NO_EXITS(270003,"优惠券不存在"),
    COUPON_NO_STOCK(270005,"优惠券库存不足"),
    COUPON_OUT_OF_LIMIT(270006,"优惠券领取超过限制次数"),
    COUPON_OUT_OF_TIME(270407,"优惠券不在领取时间范围"),
    COUPON_GET_FAIL(270407,"优惠券领取失败"),
    COUPON_RECORD_LOCK_FAIL(270409,"优惠券锁定失败"),

    /**
     * 收货地址
     */
    ADDRESS_ADD_FAIL(290001,"新增收货地址失败"),
    ADDRESS_DEL_FAIL(290002,"删除收货地址失败"),
    ADDRESS_NOT_EXIST(290003,"收货地址不存在"),


    /**
     * 文件相关
     */
    FILE_UPLOAD_USER_IMG_FAILED(600001, "用户头像文件上传失败");

    @Getter
    private String message;

    @Getter
    private int code;

    private BizCodeEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

}
