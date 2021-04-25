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
 * 订单详情表
 * </p>
 *
 * @author mo
 * @since 2021-04-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("mp_product_detail")
public class MpProductDetailDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单详情id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 订单号
     */
    private Long productOrderId;

    /**
     * 订单唯一标识
     */
    private String outTradeNo;

    /**
     * 产品id
     */
    private Long productId;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 商品图片
     */
    private String productImg;

    /**
     * 购买数量
     */
    private Integer buyNum;

    /**
     * 购物项商品单价
     */
    private BigDecimal amount;

    /**
     * 购物项商品总价格
     */
    private BigDecimal totalAmount;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;


}
