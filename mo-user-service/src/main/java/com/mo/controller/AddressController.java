package com.mo.controller;


import com.mo.model.MpAddressDO;
import com.mo.request.AddressAddRequest;
import com.mo.service.MpAddressService;
import com.mo.utils.JsonData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 收发货地址表 前端控制器
 * </p>
 *
 * @author mo
 * @since 2021-04-17
 */
@Api(tags = "收货地址模块")
@RestController
@RequestMapping("/api/address/v1")
public class AddressController {

    @Autowired
    private MpAddressService addressService;

    @ApiOperation("新增收货地址")
    @PostMapping("/add")
    public JsonData add(@ApiParam("地址对象") @RequestBody AddressAddRequest request) {

        addressService.add(request);

        return JsonData.buildSuccess();

    }

    @ApiOperation("根据id查找地址详情")
    @GetMapping("/find/{address_id}")
    public JsonData detail(@ApiParam(value = "地址id", required = true)
                           @PathVariable("address_id") long addressId) {

        MpAddressDO addressDO = addressService.detail(addressId);
        return JsonData.buildSuccess(addressDO);
    }
}

