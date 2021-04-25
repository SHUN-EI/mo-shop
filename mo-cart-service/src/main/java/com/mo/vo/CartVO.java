package com.mo.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by mo on 2021/4/25
 * 购物车对象
 */
@Data
public class CartVO {

    /**
     * 购物项目
     */
    @JsonProperty("cart_items")
    private List<CartItemVO> cartItems;

    /**
     * 购物项目总数量
     */
    @JsonProperty("total_num")
    private Integer totalNum;

    /**
     * 购物车总价格
     */
    @JsonProperty("total_amount")
    private BigDecimal totalAmount;

    /**
     * 购物车实际支付价格
     */
    @JsonProperty("actual_amount")
    private BigDecimal actualAmount;


    /**
     * 购物项目总数量 =购物项目各数量之和
     *
     * @return
     */
    public Integer getTotalNum() {
        if (this.cartItems != null) {
            int total = cartItems.stream().mapToInt(CartItemVO::getBuyNum).sum();
            return total;
        }

        return 0;
    }


    /**
     * 购物车总价格=购物项目各总价格之和
     *
     * @return
     */
    public BigDecimal getTotalAmount() {
        BigDecimal amount = new BigDecimal(0);
        if (this.cartItems != null) {
            cartItems.forEach(obj -> amount.add(obj.getTotalAmount()));
        }
        return totalAmount;
    }

    /**
     * 购物车实际支付价格
     * 实际使用时，是由前端计算用户选择的商品价格之和，再传过来的
     *
     * @return
     */
    public BigDecimal getActualAmount() {
        BigDecimal amount = new BigDecimal(0);
        if (this.cartItems != null) {
            cartItems.forEach(obj -> amount.add(obj.getTotalAmount()));
        }
        return actualAmount;
    }
}
