package com.mo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mo.interceptor.LoginInterceptor;
import com.mo.model.LoginUserDTO;
import com.mo.model.MpCouponRecordDO;
import com.mo.mapper.MpCouponRecordMapper;
import com.mo.service.CouponRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mo.vo.CouponRecordVO;
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
 * @author mo
 * @since 2021-04-22
 */
@Service
@Slf4j
public class CouponRecordServiceImpl implements CouponRecordService {

    @Autowired
    private MpCouponRecordMapper couponRecordMapper;

    @Override
    public Map<String, Object> pageCouponRecord(int page, int size) {

        LoginUserDTO loginUserDTO = LoginInterceptor.threadLocal.get();
        //第1页，每页10条
        Page<MpCouponRecordDO> pageInfo = new Page<>(page, size);

        IPage<MpCouponRecordDO> couponRecordDOPage = couponRecordMapper.selectPage(pageInfo, new QueryWrapper<MpCouponRecordDO>()
                .eq("user_id", loginUserDTO.getId())
                .orderByDesc("create_time"));

        Map<String, Object> pageMap = new HashMap<>(3);
        //总条数
        pageMap.put("total_record", couponRecordDOPage.getTotal());
        //总页数
        pageMap.put("total_page", couponRecordDOPage.getPages());

        //组装返回前端的对象
        List<CouponRecordVO> couponRecordVOList = couponRecordDOPage.getRecords().stream()
                .map(obj -> {
                    CouponRecordVO couponRecordVO = new CouponRecordVO();
                    BeanUtils.copyProperties(obj, couponRecordVO);
                    return couponRecordVO;
                }).collect(Collectors.toList());

        pageMap.put("current_data", couponRecordVOList);

        return pageMap;
    }
}
