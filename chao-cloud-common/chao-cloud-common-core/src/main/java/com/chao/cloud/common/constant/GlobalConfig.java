package com.chao.cloud.common.constant;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * 	全局配置 
 * @author 薛超
 * @since 2019年8月21日
 * @version 1.0.7
 */
public interface GlobalConfig {

	/**
	 * 日志
	 */
	@ConfigurationProperties(prefix = "log")
	@Data
	public static class Log {
		/**
		 * 生成日志的路径
		 */
		private String path;
		/**
		 * 最大历史存留/单位天
		 */
		private String maxHistory;
	}

}
