package com.mo.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Created by mo on 2021/5/3
 */
@Configuration
@Data
public class PayUrlConfig {
    /**
     * 支付成功跳转页面
     */
    @Value("${alipay.success_return_url}")
    private String alipaySuccessReturnUrl;
    /**
     * 支付成功，回调通知
     */
    @Value("${alipay.callback_url}")
    private String alipayCallbackUrl;
}
