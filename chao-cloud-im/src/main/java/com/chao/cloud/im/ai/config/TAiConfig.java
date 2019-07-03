package com.chao.cloud.im.ai.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.chao.cloud.im.ai.service.TAiRobotService;
import com.chao.cloud.im.ai.service.impl.TAiRobotServiceImpl;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties("chao.cloud.im.tai")
public class TAiConfig {

	private String appId;
	private String appKey;
	private String session; // 唯一标识

	@Bean
	public TAiRobotService tAiRobotService(TAiConfig config) {
		TAiRobotServiceImpl robotService = new TAiRobotServiceImpl();
		robotService.setConfig(config);
		return robotService;
	}

}
