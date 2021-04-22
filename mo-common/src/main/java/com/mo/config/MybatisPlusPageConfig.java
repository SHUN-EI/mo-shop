package com.mo.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by mo on 2021/4/22
 */
@Configuration
public class MybatisPlusPageConfig {

    /**
     * 新的分页插件,一缓和二缓遵循mybatis的规则
     * 需要设置 MybatisConfiguration#useDeprecatedExecutor = false 避免缓存出现问题
     * @return
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {

        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
        mybatisPlusInterceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return mybatisPlusInterceptor;

    }

//    @Bean 旧版配置
//    public PaginationInterceptor paginationInterceptor(){
//        return new PaginationInterceptor();
//    }
}
