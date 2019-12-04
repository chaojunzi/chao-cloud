package com.chao.cloud.common.web.crypto.annotaion;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.chao.cloud.common.web.crypto.Crypto;
import com.chao.cloud.common.web.crypto.advice.DecryptRequestBodyAdvice;
import com.chao.cloud.common.web.crypto.advice.EncryptResponseBodyAdvice;

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
@ConfigurationProperties(prefix = CryptoConfig.CRPYPTO_PREFIX)
public class CryptoConfig {
	public static final String CRPYPTO_PREFIX = "chao.cloud.crypto";
	private String type;
	private String charset = "UTF-8";

	@Bean
	@ConditionalOnProperty(prefix = CRPYPTO_PREFIX
			+ "decrypt", name = "enabled", havingValue = "true", matchIfMissing = true)
	public DecryptRequestBodyAdvice decryptRequestBodyAdvice(Crypto crypto, CryptoConfig config) {
		DecryptRequestBodyAdvice advice = new DecryptRequestBodyAdvice();
		advice.setCrypto(crypto);
		advice.setCharset(config.getCharset());
		return advice;
	}

	@Bean
	@ConditionalOnProperty(prefix = CRPYPTO_PREFIX
			+ "encrypt", name = "enabled", havingValue = "true", matchIfMissing = true)
	public EncryptResponseBodyAdvice encryptResponseBodyAdvice(Crypto crypto, CryptoConfig config) {
		EncryptResponseBodyAdvice advice = new EncryptResponseBodyAdvice();
		advice.setCrypto(crypto);
		advice.setCharset(config.getCharset());
		return advice;
	}

	@Bean
	public Crypto crypto(CryptoConfig config) {
		return new Crypto() {
		};
	}

}
