package com.chao.cloud.common.web.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.chao.cloud.common.web.crypto.SecretProperties;
import com.chao.cloud.common.web.crypto.SecretRequestAdvice;

/**
 * controller加密配置
 * 
 * @author 薛超
 * @since 2021年9月24日
 * @version 1.0.0
 */
@Configuration
@EnableConfigurationProperties(SecretProperties.class)
public class SecretConfig {

	@Bean
	@ConditionalOnProperty(prefix = "spring.secret", name = "enabled", havingValue = "true")
	public SecretRequestAdvice secretRequestAdvice() {
		return new SecretRequestAdvice();
	}
}
