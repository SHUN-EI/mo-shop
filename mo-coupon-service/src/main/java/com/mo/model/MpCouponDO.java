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
 * 优惠券表
 * </p>
 *
 * @author mo
 * @since 2021-04-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("mp_coupon")
public class MpCouponDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
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
    private String couponImg;

    /**
     * 优惠券标题
     */
    private String couponTitle;

    /**
     * 抵扣价格
     */
    private BigDecimal price;

    /**
     * 每人限制张数
     */
    private Integer userLimit;

    /**
     * 优惠券开始有效时间
     */
    private Date startTime;

    /**
     * 优惠券失效时间
     */
    private Date endTime;

    /**
     * 优惠券总量
     */
    private Integer publishCount;

    /**
     * 库存
     */
    private Integer stock;

    /**
     * 满多少才可以使用
     */
    private BigDecimal conditionPrice;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;


}
