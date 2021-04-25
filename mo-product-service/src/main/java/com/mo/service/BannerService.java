package com.mo.service;

import com.mo.model.BannerDO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mo.vo.BannerVO;

import java.util.List;

/**
 * @author mo
 * @since 2021-04-25
 */
public interface BannerService {

    List<BannerVO> list();

}
