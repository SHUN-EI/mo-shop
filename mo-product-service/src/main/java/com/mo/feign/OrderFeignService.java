package com.mo.feign;

import com.mo.utils.JsonData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created by mo on 2021/4/28
 */
@FeignClient(name = "mo-order-service")
public interface OrderFeignService {

    /**
     * 查询订单状态
     *
     * @param outTradeNo
     * @return
     */
    @GetMapping("/api/order/v1/query_order_state")
    JsonData queryOrderState(@RequestParam("out_trade_no") String outTradeNo);
}
