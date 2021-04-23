package com.mo.mapper;

import com.mo.model.MpCouponDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 优惠券表 Mapper 接口
 * </p>
 *
 * @author mo
 * @since 2021-04-22
 */
public interface MpCouponMapper extends BaseMapper<MpCouponDO> {

    int reduceStock(@Param("couponId") Long couponId);
}
