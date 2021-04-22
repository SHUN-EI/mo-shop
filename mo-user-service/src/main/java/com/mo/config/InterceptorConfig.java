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

    public LoginInterceptor loginInterceptor() {
        return new LoginInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor())
                //拦截路径
                .addPathPatterns("/api/user/*/**", "/api/address/*/**")
                //不拦截路径
                .excludePathPatterns(
                        "/api/user/*/getCaptchaCode",
                        "/api/user/*/sendCode",
                        "/api/user/*/login",
                        "/api/user/*/register",
                        "/api/user/*/upload");
    }
}
