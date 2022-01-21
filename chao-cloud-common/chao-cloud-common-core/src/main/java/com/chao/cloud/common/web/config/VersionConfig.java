package com.chao.cloud.common.web.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.chao.cloud.common.web.config.VersionConfig.VersionProperties;

import lombok.Data;
import lombok.Getter;

/**
 * 版本号控制
 * 
 * @author 薛超
 * @since 2021年12月10日
 * @version 1.0.0
 */
@Configuration
@EnableConfigurationProperties(VersionProperties.class)
public class VersionConfig {

	@Getter
	private static VersionProperties properties;

	@Autowired
	public void setProperties(VersionProperties properties) {
		VersionConfig.properties = properties;
	}

	@Data
	@ConfigurationProperties("application")
	public static class VersionProperties {
		/**
		 * 版本号"@project.version@"
		 */
		private String version;
		/**
		 * 时间戳 "@maven.build.timestamp@"
		 */
		private String timestamp;

	}

}
