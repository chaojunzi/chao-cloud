package com.chao.cloud.common.extra.feign.annotation;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.Request;
import feign.Retryer;

@Configuration
public class FeignBeanSupportConfig {

	/**
	 * 调用时间1min
	 */
	@Bean
	@ConfigurationProperties(prefix = "chao.cloud.feign.request")
	Request.Options feignOptions() {
		return new Request.Options(60 * 1000, /** connectTimeoutMillis **/
				60 * 1000); /** readTimeoutMillis **/
	}

	@Bean
	Retryer feignRetryer() {
		return Retryer.NEVER_RETRY;
	}

}