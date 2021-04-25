package com.mo.controller;

import com.mo.request.CartItemRequest;
import com.mo.service.CartService;
import com.mo.utils.JsonData;
import com.mo.vo.CartVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by mo on 2021/4/25
 */
@Api(tags = "购物车模块")
@RestController
@RequestMapping("/api/cart/v1")
public class CartController {

    @Autowired
    private CartService cartService;


    @ApiOperation("删除购物车商品")
    @DeleteMapping("/delete/{product_id}")
    public JsonData deleteItem(@ApiParam(value = "商品id", required = true) @PathVariable("product_id") Long productId) {

        cartService.deleteItem(productId);
        return JsonData.buildSuccess();
    }


    @ApiOperation("查看我的购物车")
    @GetMapping("/findMyCart")
    public JsonData findMyCart() {

        CartVO cartVO = cartService.findMyCart();
        return JsonData.buildSuccess(cartVO);
    }

    @ApiOperation("清空购物车")
    @DeleteMapping("/clean")
    public JsonData cleanCart() {

        cartService.clean();
        return JsonData.buildSuccess();

    }

    @ApiOperation("添加商品到购物车")
    @PostMapping("/add")
    public JsonData addToCart(@ApiParam("购物车商品对象") @RequestBody CartItemRequest request) {

        cartService.addToCart(request);
        return JsonData.buildSuccess();
    }
}
