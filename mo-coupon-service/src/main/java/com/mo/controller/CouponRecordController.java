package com.mo.controller;


import com.mo.service.CouponRecordService;
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
 * @since 2021-04-22
 */
@Api(tags = "优惠券领券记录模块")
@RestController
@RequestMapping("/api/coupon_record/v1")
public class CouponRecordController {


    @Autowired
    private CouponRecordService couponRecordService;

    @ApiOperation("分页查询优惠券领券记录列表")
    @GetMapping("/page_coupon_record")
    public JsonData pageCouponRecordList(
            @ApiParam(value = "当前页") @RequestParam(value = "page", defaultValue = "1") int page,
            @ApiParam(value = "每页显示多少条") @RequestParam(value = "size", defaultValue = "10") int size
    ) {

        Map<String, Object> pageResult = couponRecordService.pageCouponRecord(page, size);

        return JsonData.buildSuccess(pageResult);
    }
}

