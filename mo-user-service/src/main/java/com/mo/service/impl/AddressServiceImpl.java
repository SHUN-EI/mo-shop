package com.mo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mo.enums.AddressStatusEnum;
import com.mo.interceptor.LoginInterceptor;
import com.mo.model.LoginUserDTO;
import com.mo.model.MpAddressDO;
import com.mo.mapper.MpAddressMapper;
import com.mo.request.AddressAddRequest;
import com.mo.service.MpAddressService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mo.vo.AddressVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * <p>
 * 收发货地址表 服务实现类
 * </p>
 *
 * @author mo
 * @since 2021-04-17
 */
@Slf4j
@Service
public class AddressServiceImpl implements MpAddressService {

    @Autowired
    private MpAddressMapper addressMapper;

    @Override
    public int delete(long addressId) {
        LoginUserDTO loginUserDTO = LoginInterceptor.threadLocal.get();

        int rows = addressMapper.delete(new QueryWrapper<MpAddressDO>()
                .eq("id", addressId)
                .eq("user_id", loginUserDTO.getId()));

        return rows;
    }

    @Override
    public void add(AddressAddRequest request) {
        LoginUserDTO loginUserDTO = LoginInterceptor.threadLocal.get();

        MpAddressDO addressDO = new MpAddressDO();
        addressDO.setUserId(loginUserDTO.getId());
        addressDO.setCreateTime(new Date());
        addressDO.setUpdateTime(new Date());

        BeanUtils.copyProperties(request, addressDO);

        //前端传过来的是否有默认收货地址
        if (AddressStatusEnum.COMMON_STATUS.getStatus() == addressDO.getDefaultStatus()) {

            //查找数据库是否有默认收货地址
            MpAddressDO defaultAddressDO = addressMapper.selectOne(new QueryWrapper<MpAddressDO>()
                    .eq("user_Id", loginUserDTO.getId())
                    .eq("default_status", AddressStatusEnum.DEFAULT_STATUS.getStatus()));

            if (null != defaultAddressDO) {
                //修改原地址为非默认收货地址
                defaultAddressDO.setDefaultStatus(AddressStatusEnum.COMMON_STATUS.getStatus());
                addressMapper.update(defaultAddressDO, new QueryWrapper<MpAddressDO>()
                        .eq("id", defaultAddressDO.getId()));
            }
        }

        int rows = addressMapper.insert(addressDO);
        log.info("新增收货地址:rows={},data={}", rows, addressDO);
    }

    @Override
    public AddressVO detail(Long id) {
        MpAddressDO address = addressMapper.selectOne(new QueryWrapper<MpAddressDO>().eq("id", id));

        if (null == address) {
            return null;
        }

        AddressVO addressVO = new AddressVO();
        BeanUtils.copyProperties(address, addressVO);

        return addressVO;
    }
}
