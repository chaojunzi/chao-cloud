package com.chao.cloud.common.web.crypto.annotaion;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.chao.cloud.common.web.crypto.resolver.CryptoArgumentResolver;

import lombok.Data;

/**
 * 接口参数解密配置
 * 
 * @author 薛超
 * @since 2019年12月2日
 * @version 1.0.8
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "chao.cloud.crypto")
public class CryptoConfig {
	private String type;

	@Bean
	public CryptoArgumentResolver cryptoArgumentResolver() {
		return new CryptoArgumentResolver();
	}

}
