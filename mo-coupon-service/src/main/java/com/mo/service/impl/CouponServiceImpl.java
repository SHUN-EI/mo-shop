package com.mo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mo.enums.CouponCategoryEnum;
import com.mo.enums.CouponEnum;
import com.mo.model.MpCouponDO;
import com.mo.mapper.MpCouponMapper;
import com.mo.service.CouponService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mo.vo.CouponVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 优惠券表 服务实现类
 * </p>
 *
 * @author mo
 * @since 2021-04-22
 */
@Service
@Slf4j
public class CouponServiceImpl implements CouponService {

    @Autowired
    private MpCouponMapper couponMapper;

    @Override
    public Map<String, Object> pageCouponActivity(int page, int size) {
        //第1页，每页10条
        Page<MpCouponDO> pageInfo = new Page<>(page, size);

        IPage<MpCouponDO> couponDOPage = couponMapper.selectPage(pageInfo, new QueryWrapper<MpCouponDO>()
                .eq("publish", CouponEnum.PUBLISH)
                .eq("category", CouponCategoryEnum.PROMOTION)
                .orderByDesc("create_time"));


        HashMap<String, Object> pageMap = new HashMap<>(3);
        //总条数
        pageMap.put("total_record", couponDOPage.getTotal());
        //总页数
        pageMap.put("total_page", couponDOPage.getPages());

        //组装返回前端的对象
        List<CouponVO> couponVOList = couponDOPage.getRecords().stream()
                .map(obj -> {
                    CouponVO couponVO = new CouponVO();
                    BeanUtils.copyProperties(obj, couponVO);
                    return couponVO;
                }).collect(Collectors.toList());

        pageMap.put("current_data", couponVOList);

        return pageMap;
    }
}
