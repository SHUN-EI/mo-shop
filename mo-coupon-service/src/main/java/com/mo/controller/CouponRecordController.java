package com.mo.controller;


import com.mo.enums.BizCodeEnum;
import com.mo.request.LockCouponRecordRequest;
import com.mo.service.CouponRecordService;
import com.mo.utils.JsonData;
import com.mo.vo.CouponRecordVO;
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
@Api(tags = "优惠券领券记录模块")
@RestController
@RequestMapping("/api/coupon_record/v1")
public class CouponRecordController {

    @Autowired
    private CouponRecordService couponRecordService;

    @ApiOperation("RPC-锁定优惠券记录")
    @PostMapping("/lock_coupon_records")
    public JsonData lockCouponRecords(@ApiParam("优惠券锁定请求对象") @RequestBody LockCouponRecordRequest request) {

        JsonData jsonData = couponRecordService.lockCouponRecords(request);
        return JsonData.buildSuccess(jsonData);
    }


    @ApiOperation("查询优惠券领券记录详情")
    @GetMapping("/detail/{record_id}")
    public JsonData findUserCouponRecord(@PathVariable("record_id") Long recordId) {

        CouponRecordVO couponRecordVO = couponRecordService.findById(recordId);
        return couponRecordVO == null ? JsonData.buildResult(BizCodeEnum.COUPON_NO_EXITS)
                : JsonData.buildSuccess(couponRecordVO);
    }

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

