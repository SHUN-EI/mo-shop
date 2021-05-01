package com.mo.feign;

import com.mo.utils.JsonData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * Created by mo on 2021/5/1
 */
@FeignClient(name = "mo-product-service")
public interface ProductFeignService {

    /**
     * 查看商品详情
     *
     * @param productId
     * @return
     */
    @GetMapping("/api/product/v1/detail/{product_id}")
    JsonData detail(@PathVariable("product_id") Long productId);

    /**
     * 根据商品id批量查询商品
     *
     * @param productIds
     * @return
     */
    @PostMapping("/api/product/v1/findProductsByIdList")
    JsonData findProductsByIdList(@RequestBody List<Long> productIds);


}
