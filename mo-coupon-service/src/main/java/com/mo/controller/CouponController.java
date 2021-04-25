package com.mo.controller;


import com.mo.enums.CouponCategoryEnum;
import com.mo.request.NewUserCouponRequest;
import com.mo.service.CouponService;
import com.mo.utils.JsonData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author mo
 * @since 2021-04-22
 */
@Api(tags = "优惠券模块")
@RestController
@RequestMapping("/api/coupon/v1")
public class CouponController {

    @Autowired
    private CouponService couponService;

    @ApiOperation("RPC-新用户注册领券优惠券接口")
    @PostMapping("/new_user_coupon")
    public JsonData addNewUserCoupon(@ApiParam("新用户注册领券优惠券对象") @RequestBody NewUserCouponRequest request) {

        JsonData jsonData = couponService.initNewUserCoupon(request);
        return jsonData.buildSuccess(jsonData);
    }

    @ApiOperation("领取优惠券")
    @GetMapping("/add/promotion/{coupon_id}")
    public JsonData addPromotionCoupon(@ApiParam(value = "优惠券id", required = true)
                                       @PathVariable("coupon_id") Long couponId) {

        JsonData jsonData = couponService.addCoupon(couponId, CouponCategoryEnum.PROMOTION);

        return JsonData.buildSuccess(jsonData);

    }

    @ApiOperation("分页查询优惠券列表")
    @GetMapping("/page_coupon")
    public JsonData pageCouponList(
            @ApiParam(value = "当前页") @RequestParam(value = "page", defaultValue = "1") int page,
            @ApiParam(value = "每页显示多少条") @RequestParam(value = "size", defaultValue = "10") int size
    ) {

        Map<String, Object> pageMap = couponService.pageCouponActivity(page, size);
        return JsonData.buildSuccess(pageMap);
    }

}

