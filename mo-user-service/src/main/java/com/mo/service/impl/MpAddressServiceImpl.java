package com.mo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mo.model.MpAddressDO;
import com.mo.mapper.MpAddressMapper;
import com.mo.service.MpAddressService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 收发货地址表 服务实现类
 * </p>
 *
 * @author mo
 * @since 2021-04-17
 */
@Service
public class MpAddressServiceImpl implements MpAddressService {

    @Autowired
    private MpAddressMapper addressMapper;

    @Override
    public MpAddressDO detail(Long id) {
        MpAddressDO address = addressMapper.selectOne(new QueryWrapper<MpAddressDO>().eq("id", id));

        return address;
    }
}
