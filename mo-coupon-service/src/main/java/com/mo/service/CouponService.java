package com.mo.service;

import com.mo.model.MpCouponDO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * @author mo
 * @since 2021-04-22
 */
public interface CouponService {

    Map<String, Object> pageCouponActivity(int page,int size);
}
