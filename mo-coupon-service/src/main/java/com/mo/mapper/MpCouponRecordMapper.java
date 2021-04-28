package com.mo.mapper;

import com.mo.model.MpCouponRecordDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 优惠券领劵记录表 Mapper 接口
 * </p>
 *
 * @author mo
 * @since 2021-04-22
 */
public interface MpCouponRecordMapper extends BaseMapper<MpCouponRecordDO> {

    /**
     * 批量更新优惠券使用记录
     *
     * @param userId
     * @param useState
     * @param lockCouponRecordIds
     * @return
     */
    int lockUseStateBatch(@Param("userId") Long userId, @Param("useState") String useState, @Param("oldUseState") String oldUseState,@Param("lockCouponRecordIds") List<Long> lockCouponRecordIds);
}
