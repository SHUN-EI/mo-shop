package com.mo.feign;

import com.mo.request.NewUserCouponRequest;
import com.mo.utils.JsonData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Created by mo on 2021/4/26
 */
@FeignClient(name = "mo-coupon-service")
public interface CouponFeignService {

    /**
     * 新用户注册发放优惠券
     * @param request
     * @return
     */
    @PostMapping("/api/coupon/v1/new_user_coupon")
    JsonData addNewUserCoupon(NewUserCouponRequest request);

}
