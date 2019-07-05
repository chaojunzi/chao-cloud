package com.chao.cloud.im.ai.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.chao.cloud.im.ai.service.TAiRobotService;
import com.chao.cloud.im.ai.service.TAipImageClassifyService;

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
		TAiRobotService robotService = new TAiRobotService();
		robotService.setConfig(config);
		return robotService;
	}

	@Bean
	public TAipImageClassifyService tAipImageClassifyService(TAiConfig config) {
		TAipImageClassifyService aipImageClassifyService = new TAipImageClassifyService();
		aipImageClassifyService.setConfig(config);
		return aipImageClassifyService;
	}

}
