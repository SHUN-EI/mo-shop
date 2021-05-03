package com.mo.feign;

import com.mo.request.LockProductRequest;
import com.mo.utils.JsonData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Created by mo on 2021/5/3
 */
@FeignClient(name = "mo-product-service")
public interface ProductFeignService {

    /**
     * 锁定商品库存
     *
     * @param request
     * @return
     */
    @PostMapping("/api/product/v1//lock_products")
    JsonData lockProducts(@RequestBody LockProductRequest request);
}
