package com.chao.cloud.common.extra.feign.annotation;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.chao.cloud.common.extra.feign.proxy.FeignFallbackProxy;

@Configuration
public class FeignFallbackConfig {

    @Bean
    public FeignFallbackProxy FeignFallbackProxy() {
        return new FeignFallbackProxy();
    }
}
