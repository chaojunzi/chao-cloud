package com.chao.cloud.common.support.feign.annotation;

import feign.Request;
import feign.Retryer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignBeanSupportConfig {

	/**
	 * 调用时间1min
	 * 
	 * @return
	 */
	@Bean
	Request.Options feignOptions() {
		return new Request.Options(60 * 1000, /** connectTimeoutMillis **/
				60 * 1000); /** readTimeoutMillis **/
	}

	@Bean
	Retryer feignRetryer() {
		return Retryer.NEVER_RETRY;
	}

}