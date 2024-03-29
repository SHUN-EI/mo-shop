package com.mo.service;

import com.mo.enums.CouponCategoryEnum;
import com.mo.model.MpCouponDO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mo.request.NewUserCouponRequest;
import com.mo.utils.JsonData;

import java.util.Map;

/**
 * @author mo
 * @since 2021-04-22
 */
public interface CouponService {

    Map<String, Object> pageCouponActivity(int page,int size);

    JsonData addCoupon(Long couponId, CouponCategoryEnum couponCategoryEnum);

    JsonData initNewUserCoupon(NewUserCouponRequest request);
}
