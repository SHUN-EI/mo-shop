package com.mo.feign;

import com.mo.utils.JsonData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Created by mo on 2021/4/30
 */
@FeignClient(name = "mo-user-service")
public interface UserFeignService {

    /**
     * 根据id查找地址详情
     *
     * @param addressId
     * @return
     */
    @GetMapping("/api/address/v1/find/{address_id}")
    JsonData detail(@PathVariable("address_id") Long addressId);
}
