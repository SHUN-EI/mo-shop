package com.mo.controller;


import com.mo.enums.BizCodeEnum;
import com.mo.request.LockProductRequest;
import com.mo.service.ProductService;
import com.mo.utils.JsonData;
import com.mo.vo.ProductVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author mo
 * @since 2021-04-25
 */
@Api(tags = "商品模块")
@RestController
@RequestMapping("/api/product/v1")
public class ProductController {

    @Autowired
    private ProductService productService;

    @ApiOperation("RPC-锁定商品库存")
    @PostMapping("/lock_products")
    public JsonData lockProducts(@ApiParam("商品锁定请求对象") @RequestBody LockProductRequest request) {

        JsonData jsonData = productService.lockProducts(request);
        return jsonData;
    }

    @ApiOperation("商品详情")
    @GetMapping("/detail/{product_id}")
    public JsonData detail(@ApiParam(value = "商品id", required = true) @PathVariable("product_id") Long productId) {

        ProductVO productVO = productService.findById(productId);
        return productVO != null ? JsonData.buildSuccess(productVO) : JsonData.buildResult(BizCodeEnum.PRODUCT_NOT_EXISTS);
    }

    @ApiOperation("分页查询商品列表")
    @GetMapping("/page_product")
    public JsonData pageProductList(@ApiParam(value = "当前页") @RequestParam(value = "page", defaultValue = "1") int page,
                                    @ApiParam(value = "每页显示多少条") @RequestParam(value = "size", defaultValue = "10") int size) {


        Map<String, Object> pageMap = productService.pageProductList(page, size);

        return JsonData.buildSuccess(pageMap);
    }

}

