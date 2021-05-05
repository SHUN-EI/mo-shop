package com.mo.component;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.api.response.AlipayTradeWapPayResponse;
import com.mo.config.AlipayConfig;
import com.mo.config.PayUrlConfig;
import com.mo.enums.BizCodeEnum;
import com.mo.enums.ClientType;
import com.mo.exception.BizException;
import com.mo.vo.PayInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mo on 2021/5/4
 */
@Component
@Slf4j
public class AlipayStrategy implements PayStrategy {

    @Autowired
    private PayUrlConfig payUrlConfig;

    /**
     * 统一下单支付接口
     *
     * @param payInfoVO
     * @return
     */
    @Override
    public String unifiedorder(PayInfoVO payInfoVO) {

        Map<String, String> content = new HashMap<>();

        //商户订单号,64个字符以内、可包含字母、数字、下划线；需保证在商户端不重复
        content.put("out_trade_no", payInfoVO.getOutTradeNo());
        content.put("product_code", "FAST_INSTANT_TRADE_PAY");
        //订单总金额，单位为元，精确到小数点后两位
        content.put("total_amount", payInfoVO.getTotalAmount().toString());
        //商品标题/交易标题/订单标题/订单关键字等。 注意：不可使用特殊字符，如 /，=，&amp; 等。
        content.put("subject", payInfoVO.getTitle());
        //商品描述，可空
        content.put("body", payInfoVO.getDescription());

        //订单支付超时时间，毫秒转分钟
        double timeout = Math.floor(payInfoVO.getOrderPayTimeoutMills() / (1000 * 60));
        //当订单支付超时时间少于1分钟，就不能进行二次支付，前端也需要判断订单是否要关闭了
        if (timeout < 1) {
            throw new BizException(BizCodeEnum.PAY_ORDER_PAY_TIMEOUT);
        }

        // 该笔订单允许的最晚付款时间，逾期将关闭交易。取值范围：1m～15d。m-分钟，h-小时，d-天，1c-当天（1c-当天的情况下，无论交易何时创建，都在0点关闭）。 该参数数值不接受小数点， 如 1.5h，可转换为 90m。
        content.put("timeout_express", Double.valueOf(timeout).intValue() + "m");

        //端类型
        String clientType = payInfoVO.getClientType();
        //返回的表单数据
        String formData = "";

        try {
            if (ClientType.H5.name().equalsIgnoreCase(clientType)) {
                //H5手机网页支付
                AlipayTradeWapPayRequest request = new AlipayTradeWapPayRequest();
                request.setBizContent(JSON.toJSONString(content));
                request.setNotifyUrl(payUrlConfig.getAlipayCallbackUrl());
                request.setReturnUrl(payUrlConfig.getAlipaySuccessReturnUrl());

                AlipayTradeWapPayResponse aliPayResponse = AlipayConfig.getInstance().pageExecute(request);
                log.info("响应日志:aliPayResponse={}", aliPayResponse);
                if (aliPayResponse.isSuccess()) {
                    formData = aliPayResponse.getBody();
                } else {
                    log.error("支付宝构建H5表单失败:aliPayResponse={},payInfo={}", aliPayResponse, payInfoVO);
                }
            } else if (ClientType.PC.name().equalsIgnoreCase(clientType)) {
                //PC支付
                AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
                request.setBizContent(JSON.toJSONString(content));
                request.setNotifyUrl(payUrlConfig.getAlipayCallbackUrl());
                request.setReturnUrl(payUrlConfig.getAlipaySuccessReturnUrl());

                AlipayTradePagePayResponse aliPayResponse = AlipayConfig.getInstance().pageExecute(request);

                log.info("响应日志:aliPayResponse={}", aliPayResponse);
                if (aliPayResponse.isSuccess()) {
                    formData = aliPayResponse.getBody();
                } else {
                    log.error("支付宝构建PC表单失败:aliPayResponse={},payInfo={}", aliPayResponse, payInfoVO);

                }
            }
        } catch (AlipayApiException e) {
            log.error("支付宝构建表单异常:payInfo={},异常={}", payInfoVO, e);
        }

        return formData;
    }

    @Override
    public String refund(PayInfoVO payInfoVO) {
        return null;
    }


    /**
     * 统一收单 线下交易接口
     * 查询订单状态，是否支付成功
     * 支付成功 返回非空 其他返回空
     * <p>
     * 未支付
     * {"alipay_trade_query_response":{"code":"40004","msg":"Business Failed","sub_code":"ACQ.TRADE_NOT_EXIST","sub_msg":"交易不存在","buyer_pay_amount":"0.00","invoice_amount":"0.00","out_trade_no":"adbe8e8f-3b18-4c9e-b736-02c4c2e15eca","point_amount":"0.00","receipt_amount":"0.00"},"sign":"xxxxx"}
     * <p>
     * 已经支付
     * {"alipay_trade_query_response":{"code":"10000","msg":"Success","buyer_logon_id":"mqv***@sandbox.com","buyer_pay_amount":"0.00","buyer_user_id":"2088102176996700","buyer_user_type":"PRIVATE","invoice_amount":"0.00","out_trade_no":"adbe8e8f-3b18-4c9e-b736-02c4c2e15eca","point_amount":"0.00","receipt_amount":"0.00","send_pay_date":"2020-12-04 17:06:47","total_amount":"111.99","trade_no":"2020120422001496700501648498","trade_status":"TRADE_SUCCESS"},"sign":"xxxx"}
     *
     * @param payInfoVO
     * @return
     */
    @Override
    public String queryPaySuccess(PayInfoVO payInfoVO) {

        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        Map<String, String> content = new HashMap<>();

        //商户订单号,64个字符以内、可包含字母、数字、下划线；需保证在商户端不重复
        content.put("out_trade_no", payInfoVO.getOutTradeNo());
        request.setBizContent(JSON.toJSONString(content));

        AlipayTradeQueryResponse response = null;

        try {
            response = AlipayConfig.getInstance().execute(request);
            log.info("支付宝订单查询响应：{}", response.getBody());
        } catch (AlipayApiException e) {
            log.error("支付宝订单查询异常:{}", e);
        }

        if (response.isSuccess()) {
            log.info("支付宝订单状态查询成功:{}", payInfoVO);
            return response.getBody();
        } else {
            log.info("支付宝订单状态查询失败:{}", payInfoVO);
            return "";
        }
    }
}