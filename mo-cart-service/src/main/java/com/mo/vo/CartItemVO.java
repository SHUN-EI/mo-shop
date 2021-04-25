package com.mo.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Created by mo on 2021/4/25
 * 购物车项目对象
 */
@Data
public class CartItemVO {

    /**
     * 商品id
     */
    @JsonProperty("product_id")
    private Long productId;
    /**
     * 购买数量
     */
    @JsonProperty("buy_num")
    private Integer buyNum;
    /**
     * 商品标题(冗余字段)
     */
    @JsonProperty("product_title")
    private String productTitle;
    /**
     * 商品图片(冗余字段)
     */
    @JsonProperty("product_img")
    private String productImg;
    /**
     * 商品单价
     */
    private BigDecimal amount;
    /**
     * 总价格=商品单价 * 购买数量
     */
    @JsonProperty("total_amount")
    private BigDecimal totalAmount;
    /**
     * 总价格=商品单价 * 购买数量
     * @return
     */
    public BigDecimal getTotalAmount() {
        return this.amount.multiply(new BigDecimal(this.buyNum));
    }


}
