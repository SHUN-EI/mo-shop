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
 * 商品轮播图表
 * </p>
 *
 * @author mo
 * @since 2021-04-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("banner")
public class BannerDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 图片
     */
    private String img;

    /**
     * 跳转地址
     */
    private String url;

    /**
     * 权重
     */
    private Integer weight;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;


}
