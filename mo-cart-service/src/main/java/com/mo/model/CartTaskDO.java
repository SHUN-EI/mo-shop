package com.mo.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 购物车商品项目锁定表
 * </p>
 *
 * @author mo
 * @since 2021-05-02
 */
@Builder
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("cart_task")
public class CartTaskDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 商品id
     */
    private Long productId;

    /**
     * 购买数量
     */
    private Integer buyNum;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 锁定状态，锁定-LOCK,完成-FINISH,取消CANCEL
     */
    private String lockState;

    /**
     * 订单唯一标识
     */
    private String outTradeNo;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;


}
