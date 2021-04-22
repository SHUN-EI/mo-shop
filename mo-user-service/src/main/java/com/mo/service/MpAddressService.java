package com.mo.service;

import com.mo.model.MpAddressDO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mo.request.AddressAddRequest;

/**
 * <p>
 * 收发货地址表 服务类
 * </p>
 *
 * @author mo
 * @since 2021-04-17
 */
public interface MpAddressService {

    MpAddressDO detail(Long id);

    void add(AddressAddRequest request);
}
