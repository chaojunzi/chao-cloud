package com.chao.cloud.common.config.web;

import org.springframework.aop.framework.autoproxy.BeanNameAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.chao.cloud.common.convert.ConvertInterceptor;

/**
 * aop 配置
 * @author 薛超
 * @since 2019年8月1日
 * @version 1.0.5
 */
@Configuration
public class AopConfig {

	/**
	 * bean -拦截器
	 * @return {@link ConvertInterceptor}
	 */
	@Bean(name = "convert")
	public ConvertInterceptor convertInterceptor() {
		return new ConvertInterceptor();
	}

	/**
	 * 全局controller拦截
	 * @return {@link BeanNameAutoProxyCreator}
	 */
	@Bean
	public BeanNameAutoProxyCreator beanNameAutoProxyCreator() {
		BeanNameAutoProxyCreator creator = new BeanNameAutoProxyCreator();
		creator.setProxyTargetClass(true);
		creator.setBeanNames("*Controller");
		creator.setInterceptorNames("convert");
		return creator;
	}

}