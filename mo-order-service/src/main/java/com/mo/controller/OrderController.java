package com.mo.controller;


import com.mo.enums.BizCodeEnum;
import com.mo.enums.OrderPayTypeEnum;
import com.mo.model.MpOrderDO;
import com.mo.request.CreateOrderRequest;
import com.mo.service.OrderService;
import com.mo.utils.JsonData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

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

            String clientType = request.getClientType();
            String payType = request.getPayType();

            //若是支付宝网页支付，都是跳转网页，app除外
            if (payType.equalsIgnoreCase(OrderPayTypeEnum.ALIPAY.name())) {

                log.info("创建支付宝订单成功:{}", request.toString());
                writeData(response, jsonData);
            }

        } else {
            log.error("创建订单失败{}", jsonData.getData());
        }

    }

    private void writeData(HttpServletResponse response, JsonData jsonData) {
        try (PrintWriter writer = response.getWriter()) {

            response.setContentType("text/html;charset=utf-8");
            writer.write(jsonData.getData().toString());
            response.flushBuffer();
        } catch (IOException e) {
            log.error("写出响应页面异常:{}", e);
        }

    }
}

