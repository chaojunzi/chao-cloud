package com.chao.cloud.common.web.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.chao.cloud.common.web.annotation.WebConstant;
import com.chao.cloud.common.web.controller.ControllerAutoProxyCreator;
import com.chao.cloud.common.web.controller.ControllerInterceptor;

import cn.hutool.core.lang.Assert;
import lombok.Data;

/**
 * controller aop 配置
 * 
 * @author 薛超
 * @since 2019年8月1日
 * @version 1.0.5
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "chao.cloud.controller")
public class ControllerConfig {

	private String interceptorNames = WebConstant.CONTROLLER_INTERCEPTOR;
	private boolean proxyTargetClass = true;
	private Integer order = WebConstant.CONTROLLER_ORDER;

	/**
	 * bean -拦截器
	 * 
	 * @return {@link ControllerInterceptor}
	 */
	@Bean(name = WebConstant.CONTROLLER_INTERCEPTOR)
	@ConditionalOnMissingBean(ControllerInterceptor.class)
	public ControllerInterceptor convertInterceptor(ControllerConfig config) {
		ControllerInterceptor interceptor = new ControllerInterceptor();
		interceptor.setOrder(config.getOrder());
		return interceptor;
	}

	/**
	 * 全局controller拦截
	 * 
	 * @return {@link ControllerAutoProxyCreator}
	 */
	@Bean
	public ControllerAutoProxyCreator controllerAutoProxyCreator(ControllerConfig config) {
		ControllerAutoProxyCreator creator = new ControllerAutoProxyCreator();
		creator.setProxyTargetClass(config.proxyTargetClass);
		Assert.notBlank(config.getInterceptorNames(), "interceptorNames 不能为空");
		creator.setInterceptorNames(config.getInterceptorNames());
		return creator;
	}

}