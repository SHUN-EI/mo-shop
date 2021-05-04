package com.mo.config;

import com.mo.interceptor.LoginInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Created by mo on 2021/4/22
 */
@Configuration
@Slf4j
public class InterceptorConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor())
                //拦截路径
                .addPathPatterns("/api/order/*/**")
                //不拦截路径
                .excludePathPatterns("/api/pay/*/callback/**","/api/order/*/query_order_state");
    }
}
