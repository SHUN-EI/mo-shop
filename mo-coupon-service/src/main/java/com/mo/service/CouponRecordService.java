package com.mo.service;

import com.mo.model.MpCouponRecordDO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 *
 * @author mo
 * @since 2021-04-22
 */
public interface CouponRecordService {

    Map<String,Object> pageCouponRecord(int page,int size);

}
