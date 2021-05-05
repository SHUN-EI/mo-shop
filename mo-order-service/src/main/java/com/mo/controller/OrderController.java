package com.mo.controller;


import com.mo.constant.CacheKey;
import com.mo.enums.BizCodeEnum;
import com.mo.enums.OrderPayTypeEnum;
import com.mo.interceptor.LoginInterceptor;
import com.mo.model.LoginUserDTO;
import com.mo.model.MpOrderDO;
import com.mo.request.CreateOrderRequest;
import com.mo.request.OrderListRequest;
import com.mo.service.OrderService;
import com.mo.utils.CommonUtil;
import com.mo.utils.HttpUtil;
import com.mo.utils.JsonData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author mo
 * @since 2021-04-26
 */
@Api(tags = "订单模块")
@RestController
@RequestMapping("/api/order/v1")
@Slf4j
public class OrderController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 获取提交订单令牌
     *
     * @return
     */
    @ApiOperation("获取提交订单令牌")
    @GetMapping("/getOrderToken")
    public JsonData getOrderToken() {
        LoginUserDTO loginUserDTO = LoginInterceptor.threadLocal.get();
        String key = String.format(CacheKey.SUBMIT_ORDER_TOKEN_KEY, loginUserDTO.getId());
        String token = CommonUtil.getStringNumRandom(32);

        redisTemplate.opsForValue().set(key, token, 30, TimeUnit.MINUTES);

        return JsonData.buildSuccess(token);
    }

    /**
     * 分页查询订单列表
     *
     * @param request
     * @return
     */
    @ApiOperation("分页查询订单列表")
    @PostMapping("/pageOrderList")
    public JsonData pageOrderList(@ApiParam("订单列表对象") @RequestBody OrderListRequest request) {

        Map<String, Object> pageResult = orderService.pageOrderList(request);

        return JsonData.buildSuccess(pageResult);
    }

    /**
     * 查询订单状态
     * 此接口没有登录拦截，远程调用考虑安全的话，可以增加一个密钥进行RPC通信
     * 不加密钥也行，因为此接口只是返回一个订单的状态，没有太多敏感信息
     *
     * @param outTradeNo
     * @return
     */
    @ApiOperation("查询订单状态")
    @GetMapping("/query_order_state")
    public JsonData queryOrderState(@ApiParam("订单号") @RequestParam("out_trade_no") String outTradeNo) {

        JsonData jsonData = orderService.queryOrderState(outTradeNo);
        return jsonData == null ? JsonData.buildResult(BizCodeEnum.ORDER_CONFIRM_NOT_EXIST)
                : JsonData.buildSuccess(((MpOrderDO) jsonData.getData()).getState());
    }

    @ApiOperation("创建订单")
    @PostMapping("/createOrder")
    public void createOrder(@ApiParam("创建订单对象") @RequestBody CreateOrderRequest request, HttpServletResponse response) {

        JsonData jsonData = orderService.createOrder(request);
        if (jsonData.getCode() == 0) {
            log.info("创建订单成功:{}", jsonData.getData());
            HttpUtil.writeData(response, jsonData);
        } else {
            log.error("创建订单失败:{}", jsonData.getData());
            CommonUtil.sendJsonMessage(response, jsonData);
        }
    }
}

