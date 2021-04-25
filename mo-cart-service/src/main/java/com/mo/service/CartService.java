package com.mo.service;

import com.mo.request.CartItemRequest;

/**
 * Created by mo on 2021/4/25
 */
public interface CartService {

    void addToCart(CartItemRequest request);

    void clean();
}
