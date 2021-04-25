package com.mo.service;

import com.mo.model.MpProductDO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * @author mo
 * @since 2021-04-25
 */
public interface MpProductService {

    Map<String, Object> pageProductList(int page, int size);
}
