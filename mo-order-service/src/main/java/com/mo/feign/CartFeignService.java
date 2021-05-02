package com.mo.feign;

import com.mo.utils.JsonData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * Created by mo on 2021/5/2
 */
@FeignClient(name = "mo-cart-service")
public interface CartFeignService {

    /**
     * 获取对应订单购物车里面的商品信息
     * @param productIds
     * @return
     */
    @PostMapping("/api/cart/v1/confirmOrderCartItems")
    JsonData confirmOrderCartItems(@RequestBody List<Long> productIds);
}
