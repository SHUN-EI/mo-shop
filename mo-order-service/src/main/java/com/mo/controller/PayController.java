package com.mo.controller;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.alipay.api.response.AlipayTradeWapPayResponse;
import com.mo.config.AlipayConfig;
import com.mo.config.PayUrlConfig;
import com.mo.enums.OrderCodeEnum;
import com.mo.utils.OrderCodeGenerateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.spring.web.json.Json;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mo on 2021/5/3
 */
@RequestMapping("/api/pay/v1")
@RestController
@Slf4j
public class PayController {

    @Autowired
    private OrderCodeGenerateUtil orderCodeGenerateUtil;
    @Autowired
    private PayUrlConfig payUrlConfig;

    @GetMapping("/testPay")
    public void testAliPay(HttpServletResponse response) {

        Map<String, String> content = new HashMap<>();
        //商户订单号,64个字符以内、可包含字母、数字、下划线；需保证在商户端不重复
        String outTradeNo = orderCodeGenerateUtil.generateOrderCode(OrderCodeEnum.XD);

        log.info("订单号:{}", outTradeNo);
        content.put("out_trade_no", outTradeNo);
        content.put("product_code", "FAST_INSTANT_TRADE_PAY");
        //订单总金额，单位为元，精确到小数点后两位
        content.put("total_amount", String.valueOf("111.99"));
        //商品标题/交易标题/订单标题/订单关键字等。 注意：不可使用特殊字符，如 /，=，&amp; 等。
        content.put("subject", "杯子");
        //商品描述，可空
        content.put("body", "好的杯子");
        // 该笔订单允许的最晚付款时间，逾期将关闭交易。取值范围：1m～15d。m-分钟，h-小时，d-天，1c-当天（1c-当天的情况下，无论交易何时创建，都在0点关闭）。 该参数数值不接受小数点， 如 1.5h，可转换为 90m。
        content.put("timeout_express", "5m");

        //发送交易请求
        AlipayTradeWapPayRequest request = new AlipayTradeWapPayRequest();
        request.setBizContent(JSON.toJSONString(content));
        request.setNotifyUrl(payUrlConfig.getAlipayCallbackUrl());
        request.setReturnUrl(payUrlConfig.getAlipaySuccessReturnUrl());

        try {
            AlipayTradeWapPayResponse aliPayresponse = AlipayConfig.getInstance().pageExecute(request);
            if (aliPayresponse.isSuccess()) {
                log.info("调用支付宝支付接口成功");

                String formData = aliPayresponse.getBody();
                response.setContentType("text/html;charset=UTF-8");
                response.getWriter().write(formData);
                response.getWriter().flush();
                response.getWriter().close();
            } else {
                log.error("调用支付宝支付接口失败");
            }
        } catch (AlipayApiException | IOException e) {
            log.error("调用支付宝支付接口异常:{}", request);
        }
    }
}
