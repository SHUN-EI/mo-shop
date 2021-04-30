package com.mo.service;

import com.mo.model.MpAddressDO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mo.request.AddressAddRequest;
import com.mo.vo.AddressVO;

import java.util.List;

/**
 * <p>
 * 收发货地址表 服务类
 * </p>
 *
 * @author mo
 * @since 2021-04-17
 */
public interface AddressService {

    AddressVO detail(Long id);

    void add(AddressAddRequest request);

    int delete(Long addressId);

    List<AddressVO> list();
}
