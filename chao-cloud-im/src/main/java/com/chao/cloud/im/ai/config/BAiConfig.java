package com.chao.cloud.im.ai.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.chao.cloud.im.ai.service.BAiUnitService;
import com.chao.cloud.im.ai.service.impl.BAiUnitServiceImpl;

import cn.hutool.core.lang.Console;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import lombok.Data;

@Data
@Configuration
@ConfigurationProperties("chao.cloud.im.bai")
public class BAiConfig {

	private String appId;
	private String apiKey;
	private String secretKey; // 唯一标识
	private String robot;
	private String accessToken;

	@Bean
	public BAiUnitService bAiRobotService(BAiConfig config) {
		BAiUnitServiceImpl robotService = new BAiUnitServiceImpl();
		robotService.setConfig(config);
		return robotService;
	}

	public void genAccessToken() {
		String url = "https://aip.baidubce.com/oauth/2.0/token?grant_type=client_credentials&client_id=" + apiKey
				+ "&client_secret=" + secretKey;
		String json = HttpUtil.get(url);
		Console.log(JSONUtil.toJsonPrettyStr(json));

	}
}
