package com.mo.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 优惠券库存锁定任务表
 * </p>
 *
 * @author mo
 * @since 2021-04-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("coupon_task")
public class CouponTaskDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 优惠券记录id
     */
    private Long couponRecordId;

    /**
     * 订单唯一标识
     */
    private String outTradeNo;

    /**
     * 锁定状态，锁定-LOCK,完成-FINISH,取消CANCEL
     */
    private String lockState;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;


}
