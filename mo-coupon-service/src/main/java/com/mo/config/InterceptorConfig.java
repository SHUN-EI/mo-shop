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
                .addPathPatterns("/api/coupon/*/**", "/api/couponRecord/*/**")
                //不拦截路径
                .excludePathPatterns(
                        "/api/coupon/*/page_coupon",
                        "/api/coupon/*/new_user_coupon");
    }
}
