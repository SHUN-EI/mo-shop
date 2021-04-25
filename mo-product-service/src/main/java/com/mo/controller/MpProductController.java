package com.mo.controller;


import com.mo.service.MpProductService;
import com.mo.utils.JsonData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author mo
 * @since 2021-04-25
 */
@Api(tags = "商品模块")
@RestController
@RequestMapping("/api/product/v1")
public class MpProductController {

    @Autowired
    private MpProductService productService;

    @ApiOperation("分页查询商品列表")
    @GetMapping("/page_product")
    public JsonData pageProductList(@ApiParam(value = "当前页") @RequestParam(value = "page", defaultValue = "1") int page,
                                    @ApiParam(value = "每页显示多少条") @RequestParam(value = "size", defaultValue = "10") int size) {


        Map<String, Object> pageMap = productService.pageProductList(page, size);

        return JsonData.buildSuccess(pageMap);
    }

}

