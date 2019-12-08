package com.chao.cloud.common.web.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.chao.cloud.common.web.annotation.WebConstant;
import com.chao.cloud.common.web.sign.SignAutoProxyCreator;
import com.chao.cloud.common.web.sign.SignInterceptor;

import cn.hutool.core.lang.Assert;
import lombok.Data;

/**
 * 接口签名配置
 * 
 * @author 薛超
 * @since 2019年12月8日
 * @version 1.0.8
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "chao.cloud.sign")
public class SignConfig {

	private String interceptorNames = WebConstant.SIGN_INTERCEPTOR;
	private boolean proxyTargetClass = true;
	private Integer order = WebConstant.SIGN_ORDER;
	private Integer timeout = 60;// 60秒
	/**
	 * 签名算法类型 MD5|SHA1|SHA256
	 */
	private String signType = "MD5";

	/**
	 * Sign-拦截器
	 * 
	 * @return {@link SignInterceptor}
	 */
	@Bean(name = WebConstant.SIGN_INTERCEPTOR)
	@ConditionalOnMissingBean(SignInterceptor.class)
	public SignInterceptor signInterceptor(SignConfig config) {
		SignInterceptor interceptor = new SignInterceptor();
		interceptor.setOrder(config.getOrder());
		interceptor.setConfig(config);
		return interceptor;
	}

	/**
	 * 全局Sign拦截
	 * 
	 * @return {@link SignAutoProxyCreator}
	 */
	@Bean
	public SignAutoProxyCreator SignAutoProxyCreator(SignConfig config) {
		SignAutoProxyCreator creator = new SignAutoProxyCreator();
		creator.setProxyTargetClass(config.proxyTargetClass);
		Assert.notBlank(config.getInterceptorNames(), "interceptorNames 不能为空");
		creator.setInterceptorNames(config.getInterceptorNames());
		return creator;
	}

}