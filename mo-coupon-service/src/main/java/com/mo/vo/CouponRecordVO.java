package com.mo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by mo on 2021/4/24
 */
@Data
public class CouponRecordVO {

    /**
     * 优惠券领券记录id
     */
    private Long id;

    /**
     * 优惠券id
     */
    @JsonProperty("coupon_Id")
    private Long couponId;

    /**
     * 使用状态：NEW-可用,USED-已使用,EXPIRED-过期;
     */
    @JsonProperty("use_state")
    private String useState;

    /**
     * 用户id
     */
    @JsonProperty("user_id")
    private Long userId;

    /**
     * 用户名
     */
    @JsonProperty("user_name")
    private String userName;

    /**
     * 优惠券标题
     */
    @JsonProperty("coupon_title")
    private String couponTitle;

    /**
     * 开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss", locale = "zh", timezone = "GMT+8")
    @JsonProperty("start_time")
    private Date startTime;

    /**
     * 结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss", locale = "zh", timezone = "GMT+8")
    @JsonProperty("end_time")
    private Date endTime;

    /**
     * 订单id
     */
    @JsonProperty("order_id")
    private Long orderId;

    /**
     * 抵扣价格
     */
    private BigDecimal price;

    /**
     * 满多少才可以使用
     */
    @JsonProperty("condition_price")
    private BigDecimal conditionPrice;

    /**
     * 创建时间获得时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss", locale = "zh", timezone = "GMT+8")
    @JsonProperty("create_time")
    private Date createTime;

}
