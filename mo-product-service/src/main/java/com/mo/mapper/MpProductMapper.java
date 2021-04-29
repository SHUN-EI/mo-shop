package com.mo.mapper;

import com.mo.model.MpProductDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 商品表 Mapper 接口
 * </p>
 *
 * @author mo
 * @since 2021-04-25
 */
public interface MpProductMapper extends BaseMapper<MpProductDO> {

    /**
     * 锁定商品库存
     *
     * @param productId
     * @param buyNum
     * @return
     */
    int lockProductStock(@Param("productId") Long productId, @Param("buyNum") Integer buyNum);

    /**
     * 解锁商品库存
     *
     * @param productId
     * @param buyNum
     */
    void unlockProductStock(@Param("productId") Long productId, @Param("buyNum") Integer buyNum);
}
