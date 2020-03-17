package com.chao.cloud.common.web.config;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.ConversionService;
import org.springframework.format.support.FormattingConversionServiceFactoryBean;

import com.chao.cloud.common.core.ApplicationRefreshed;

import cn.hutool.extra.spring.SpringUtil;

/**
 * spring 核心配置
 * 
 * @author 薛超
 * @since 2019年8月1日
 * @version 1.0.5
 */
@Configuration
public class SpringConfig {

	/**
	 * 全局spring容器
	 * 
	 * @param applicationContext {@link ApplicationContext}
	 * @return {@link SpringUtil}
	 */
	@Bean
	@Order(value = Integer.MIN_VALUE)
	public SpringUtil springContextUtil(ApplicationContext applicationContext) {
		SpringUtil util = new SpringUtil();
		util.setApplicationContext(applicationContext);
		return util;
	}

	@Bean
	public ApplicationRefreshed application() {
		return new ApplicationRefreshed();
	}

	@Bean
	public ConversionService conversionService() {
		FormattingConversionServiceFactoryBean factoryBean = new FormattingConversionServiceFactoryBean();
		factoryBean.afterPropertiesSet();
		return factoryBean.getObject();
	}

}
