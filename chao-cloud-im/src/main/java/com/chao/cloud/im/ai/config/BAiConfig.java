package com.chao.cloud.im.ai.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.chao.cloud.im.ai.service.AipUnitService;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties("chao.cloud.im.bai")
public class BAiConfig {

	private String appId;
	private String apiKey;
	private String secretKey; // 唯一标识

	@Bean
	public AipUnitService AipUnit(BAiConfig config) {
		AipUnitService aipUnit = new AipUnitService(config.appId, config.apiKey, config.secretKey);
		return aipUnit;
	}

}
