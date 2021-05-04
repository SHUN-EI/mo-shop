package com.mo.feign;

import com.mo.request.LockCouponRecordRequest;
import com.mo.utils.JsonData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Created by mo on 2021/5/3
 */
@FeignClient(name = "mo-coupon-service")
public interface CouponRecordFeignService {

    /**
     * 查询用户优惠券领券记录详情，防止水平越权
     *
     * @param recordId
     * @return
     */
    @GetMapping("/api/couponRecord/v1/detail/{record_id}")
    JsonData findUserCouponRecord(@PathVariable("record_id") Long recordId);

    /**
     * 锁定优惠券记录
     *
     * @param lockCouponRecordRequest
     * @return
     */
    @PostMapping("/api/couponRecord/v1/lock_coupon_records")
    JsonData lockCouponRecords(@RequestBody LockCouponRecordRequest lockCouponRecordRequest);
}
