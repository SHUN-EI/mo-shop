package com.mo.mapper;

import com.mo.model.MpOrderDetailDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 订单详情表 Mapper 接口
 * </p>
 *
 * @author mo
 * @since 2021-04-26
 */
public interface MpOrderDetailMapper extends BaseMapper<MpOrderDetailDO> {

    /**
     * 批量插入订单详情对象
     *
     * @param orderDetailList
     */
    void insertBatch(@Param("orderDetailList") List<MpOrderDetailDO> orderDetailList);
}
