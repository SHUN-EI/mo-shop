package com.mo.service;

import com.mo.request.CartItemRequest;
import com.mo.request.LockCartItemsRequest;
import com.mo.utils.JsonData;
import com.mo.vo.CartItemVO;
import com.mo.vo.CartVO;

import java.util.List;

/**
 * Created by mo on 2021/4/25
 */
public interface CartService {

    void addToCart(CartItemRequest request);

    void clean();

    CartVO findMyCart();

    void deleteItem(Long productId);

    void changeItemNum(CartItemRequest request);

    List<CartItemVO> confirmOrderCartItems(List<Long> productIds);

    JsonData lockCartItems(LockCartItemsRequest request);
}
