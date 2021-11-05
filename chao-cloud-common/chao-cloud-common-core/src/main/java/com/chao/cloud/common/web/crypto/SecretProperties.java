package com.chao.cloud.common.web.crypto;

import java.lang.annotation.Annotation;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties("spring.secret")
public class SecretProperties {
	/**
	 * 是否开启
	 */
	private boolean enabled = false;
	/**
	 * 加密类型
	 */
	private SecretEnum type = SecretEnum.AES;
	/**
	 * 加密密钥
	 */
	private String key = "A012345678912345";
	/**
	 * 扫描自定义注解
	 */
	private Class<? extends Annotation> annotationClass = SecretBody.class;

}