package com.chao.cloud.common.extra.access.annotation;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.chao.cloud.common.extra.access.proxy.AccessLimitProxy;

@Configuration
public class AccessLimitConfig {

	@Bean
	public AccessLimitProxy AccessLimitProxy() {
		return new AccessLimitProxy();
	}

}
