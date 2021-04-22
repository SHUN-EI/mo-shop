package com.mo.model;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 优惠券领劵记录表
 * </p>
 *
 * @author mo
 * @since 2021-04-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("mp_coupon_record")
public class MpCouponRecordDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 优惠券id
     */
    private Long couponId;

    /**
     * 使用状态：NEW-可用,USED-已使用,EXPIRED-过期;
     */
    private String useState;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 优惠券标题
     */
    private String couponTitle;

    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 结束时间
     */
    private Date endTime;

    /**
     * 订单id
     */
    private Long orderId;

    /**
     * 抵扣价格
     */
    private BigDecimal price;

    /**
     * 满多少才可以使用
     */
    private BigDecimal conditionPrice;

    /**
     * 创建时间获得时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;


}
