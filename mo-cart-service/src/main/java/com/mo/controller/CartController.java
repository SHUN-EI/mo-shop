package com.mo.controller;

import com.mo.request.CartItemRequest;
import com.mo.request.LockCartItemsRequest;
import com.mo.service.CartService;
import com.mo.utils.JsonData;
import com.mo.vo.CartItemVO;
import com.mo.vo.CartVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.checkerframework.checker.index.qual.PolySameLen;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by mo on 2021/4/25
 */
@Api(tags = "购物车模块")
@RestController
@RequestMapping("/api/cart/v1")
public class CartController {

    @Autowired
    private CartService cartService;


    @ApiOperation("RPC-锁定购物车商品项目")
    @PostMapping("/lock_cartItems")
    public JsonData lockCartItems(@ApiParam("购物车商品项目锁定对象") @RequestBody LockCartItemsRequest request) {

        JsonData jsonData = cartService.lockCartItems(request);
        return jsonData;
    }


    /**
     * 用于订单服务，确认订单，获取购物车对应的商品详情信息
     * 会清空购物车对应的商品
     *
     * @param productIds
     * @return
     */
    @ApiOperation("获取对应订单购物车里面的商品信息")
    @PostMapping("/confirmOrderCartItems")
    public JsonData confirmOrderCartItems(@ApiParam("商品id列表") @RequestBody List<Long> productIds) {
        List<CartItemVO> cartItemVOList = cartService.confirmOrderCartItems(productIds);
        return JsonData.buildSuccess(cartItemVOList);
    }

    @ApiOperation("修改购物车商品数量")
    @PostMapping("/changeItemNum")
    public JsonData changeItemNum(@ApiParam("购物车商品对象") @RequestBody CartItemRequest request) {

        cartService.changeItemNum(request);
        return JsonData.buildSuccess();
    }

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
