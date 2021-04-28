package com.mo.service;

import com.mo.model.CouponRecordMessage;
import com.mo.model.MpCouponRecordDO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mo.request.LockCouponRecordRequest;
import com.mo.utils.JsonData;
import com.mo.vo.CouponRecordVO;

import java.util.Map;

/**
 * @author mo
 * @since 2021-04-22
 */
public interface CouponRecordService {

    Map<String, Object> pageCouponRecord(int page, int size);

    CouponRecordVO findById(Long recordId);

    JsonData lockCouponRecords(LockCouponRecordRequest request);

    boolean releaseCouponRecord(CouponRecordMessage recordMessage);
}
