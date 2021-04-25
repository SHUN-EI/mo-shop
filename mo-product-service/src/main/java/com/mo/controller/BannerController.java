package com.mo.controller;


import com.mo.service.BannerService;
import com.mo.utils.JsonData;
import com.mo.vo.BannerVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author mo
 * @since 2021-04-25
 */
@Api(tags = "商品轮播图模块")
@RestController
@RequestMapping("/api/product/banner/v1")
public class BannerController {

    @Autowired
    private BannerService bannerService;

    @ApiOperation("商品轮播图列表")
    @GetMapping("/list")
    public JsonData list() {
        List<BannerVO> bannerVOList = bannerService.list();
        return JsonData.buildSuccess(bannerVOList);
    }

}

