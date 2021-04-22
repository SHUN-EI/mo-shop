package com.mo.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by mo on 2021/4/23
 */
@Data
public class CouponVO {

    private Long id;

    /**
     * 优惠卷类型[NEW_USER-注册赠券，TASK-任务卷，PROMOTION-促销劵]
     */
    private String category;

    /**
     * 发布状态, PUBLISH-发布，DRAFT-草稿，OFFLINE-下线
     */
    private String publish;

    /**
     * 优惠券图片
     */
    @JsonProperty("coupon_img")
    private String couponImg;

    /**
     * 优惠券标题
     */
    @JsonProperty("coupon_title")
    private String couponTitle;

    /**
     * 抵扣价格
     */
    private BigDecimal price;

    /**
     * 每人限制张数
     */
    @JsonProperty("user_limit")
    private Integer userLimit;

    /**
     * 优惠券开始有效时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss", locale = "zh", timezone = "GMT+8")
    @JsonProperty("start_time")
    private Date startTime;

    /**
     * 优惠券失效时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss", locale = "zh", timezone = "GMT+8")
    @JsonProperty("end_time")
    private Date endTime;

    /**
     * 优惠券总量
     */
    @JsonProperty("publish_count")
    private Integer publishCount;

    /**
     * 库存
     */
    private Integer stock;

    /**
     * 满多少才可以使用
     */
    @JsonProperty("condition_price")
    private BigDecimal conditionPrice;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss", locale = "zh", timezone = "GMT+8")
    @JsonProperty("create_time")
    private Date createTime;

}
