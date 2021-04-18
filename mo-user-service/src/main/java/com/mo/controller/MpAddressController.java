package com.mo.controller;


import com.mo.service.MpAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 收发货地址表 前端控制器
 * </p>
 *
 * @author mo
 * @since 2021-04-17
 */
@RestController
@RequestMapping("/api/address/v1/")
public class MpAddressController {

    @Autowired
    private MpAddressService addressService;


    @GetMapping("/find/{address_id}")
    public  Object detail(@PathVariable("address_id") long addressId){
        return  addressService.detail(addressId);
    }
}

