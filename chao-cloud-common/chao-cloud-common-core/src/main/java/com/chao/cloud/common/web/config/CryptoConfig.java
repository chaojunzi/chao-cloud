package com.chao.cloud.common.web.config;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.chao.cloud.common.exception.BusinessException;
import com.chao.cloud.common.web.annotation.WebConstant;
import com.chao.cloud.common.web.crypto.CryptoAutoProxyCreator;
import com.chao.cloud.common.web.crypto.CryptoController;
import com.chao.cloud.common.web.crypto.CryptoInterceptor;
import com.chao.cloud.common.web.crypto.CryptoTypeEnum;

import cn.hutool.core.lang.Assert;
import cn.hutool.crypto.SecureUtil;
import lombok.Data;

/**
 * 接口参数解密配置
 * 
 * @author 薛超
 * @since 2019年12月6日
 * @version 1.0.8
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "chao.cloud.crypto")
public class CryptoConfig implements InitializingBean {
	private String interceptorNames = WebConstant.CRYPTO_INTERCEPTOR;
	private boolean proxyTargetClass = true;
	private Integer order = WebConstant.CRYPTO_ORDER;
	private String charset = "UTF-8";
	// 密钥配置
	private Map<String, CryptoKey> keys = new HashMap<>();
	// 加解密服务
	private Map<CryptoTypeEnum, Object> cryptoService = new ConcurrentHashMap<>();

	@SuppressWarnings("unchecked")
	public <T> T getCryptoService(CryptoTypeEnum type) {
		Object crypto = cryptoService.get(type);
		if (crypto == null) {
			throw new BusinessException(type + " : 密钥未配置");
		}
		return (T) crypto;
	}

	/**
	 * Crypto-拦截器
	 * 
	 * @return {@link CryptoInterceptor}
	 */
	@Bean(name = WebConstant.CRYPTO_INTERCEPTOR)
	@ConditionalOnMissingBean(CryptoInterceptor.class)
	public CryptoInterceptor cryptoInterceptor(CryptoConfig config) {
		CryptoInterceptor interceptor = new CryptoInterceptor();
		interceptor.setOrder(config.getOrder());
		interceptor.setConfig(config);
		return interceptor;
	}

	/**
	 * 全局Crypto拦截
	 * 
	 * @return {@link CryptoAutoProxyCreator}
	 */
	@Bean
	public CryptoAutoProxyCreator cryptoAutoProxyCreator(CryptoConfig config) {
		CryptoAutoProxyCreator creator = new CryptoAutoProxyCreator();
		creator.setProxyTargetClass(config.proxyTargetClass);
		Assert.notBlank(config.getInterceptorNames(), "interceptorNames 不能为空");
		creator.setInterceptorNames(config.getInterceptorNames());
		return creator;
	}

	@Bean
	public CryptoController cryptoController(CryptoConfig config) {
		CryptoController cryptoController = new CryptoController();
		cryptoController.setCryptoConfig(config);
		return cryptoController;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// 配置解密服务
		if (keys.size() > 0) {
			keys.forEach((k, v) -> {
				CryptoTypeEnum type = CryptoTypeEnum.getByType(k);
				switch (type) {
				case DES:
					Assert.notBlank(v.secretKey, "secretKey 不能为空");
					cryptoService.put(type, SecureUtil.des(v.secretKey.getBytes()));
					break;
				case RSA:
					Assert.notBlank(v.privateKey, "privateKey 不能为空");
					Assert.notBlank(v.publicKey, "publicKey 不能为空");
					cryptoService.put(type, SecureUtil.rsa(v.privateKey, v.publicKey));
					break;

				default:
					break;
				}
			});
		}
	}

	@Data
	public static class CryptoKey {
		/**
		 * des
		 */
		private String secretKey;
		/**
		 * rsa-非对称加密
		 */
		private String privateKey;
		private String publicKey;
	}
}
