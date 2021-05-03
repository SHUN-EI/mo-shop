package com.mo.mapper;

import com.mo.model.MpOrderDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 订单表 Mapper 接口
 * </p>
 *
 * @author mo
 * @since 2021-04-26
 */
public interface MpOrderMapper extends BaseMapper<MpOrderDO> {

    /**
     * 更新订单状态
     *
     * @param outTradeNo
     * @param newState
     * @param oldState
     */
    void updateOrderState(@Param("outTradeNo") String outTradeNo, @Param("newState") String newState, @Param("oldState") String oldState);
}
