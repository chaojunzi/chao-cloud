package com.chao.cloud.common.extra.crypto.annotation;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

/**
 * 接口参数解密配置
 * 
 * @author 薛超
 * @since 2019年11月28日
 * @version 1.0.8
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "chao.cloud.crypto")
public class CryptoConfig {

	/**
	 * 使用多种配置 不要局限
	 */
	private String type;

}
