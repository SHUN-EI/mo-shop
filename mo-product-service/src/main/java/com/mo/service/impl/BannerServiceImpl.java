package com.mo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mo.model.BannerDO;
import com.mo.mapper.BannerMapper;
import com.mo.service.BannerService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mo.vo.BannerVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author mo
 * @since 2021-04-25
 */
@Service
public class BannerServiceImpl implements BannerService {

    @Autowired
    private BannerMapper bannerMapper;

    @Override
    public List<BannerVO> list() {

        List<BannerDO> bannerDOList = bannerMapper.selectList(new QueryWrapper<BannerDO>().orderByDesc("weight"));

        if (bannerDOList.size() > 0) {
            List<BannerVO> bannerVOList = bannerDOList.stream().map(obj -> {
                BannerVO bannerVO = new BannerVO();
                BeanUtils.copyProperties(obj, bannerVO);
                return bannerVO;
            }).collect(Collectors.toList());

            return bannerVOList;
        }

        return null;
    }
}
