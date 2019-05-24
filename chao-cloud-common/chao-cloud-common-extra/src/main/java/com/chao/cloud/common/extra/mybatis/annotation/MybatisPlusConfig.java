package com.chao.cloud.common.extra.mybatis.annotation;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.baomidou.mybatisplus.extension.plugins.OptimisticLockerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;

@Configuration
public class MybatisPlusConfig {
    /**
     * 乐观锁 插件
     * 
     * @return
     */
    @Bean
    public OptimisticLockerInterceptor optimisticLoker() {
        return new OptimisticLockerInterceptor();
    }

    /**
     * 分页插件
     */
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        return new PaginationInterceptor();
    }

}
