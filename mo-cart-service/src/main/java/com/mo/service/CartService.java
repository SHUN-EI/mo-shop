package com.mo.service;

import com.mo.request.CartItemRequest;
import com.mo.vo.CartVO;

/**
 * Created by mo on 2021/4/25
 */
public interface CartService {

    void addToCart(CartItemRequest request);

    void clean();

    CartVO findMyCart();

    void deleteItem(Long productId);
}
