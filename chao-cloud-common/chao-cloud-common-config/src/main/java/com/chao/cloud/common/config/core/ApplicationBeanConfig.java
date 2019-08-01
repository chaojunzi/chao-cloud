package com.chao.cloud.common.config.core;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.format.support.FormattingConversionServiceFactoryBean;

import com.chao.cloud.common.core.ApplicationRefreshed;
import com.chao.cloud.common.core.SpringContextUtil;

/**
 * spring 核心配置
 * @author 薛超
 * @since 2019年8月1日
 * @version 1.0.5
 */
@Configuration
public class ApplicationBeanConfig {

	/**
	 * 全局spring容器
	 * @param applicationContext {@link ApplicationContext}
	 * @return {@link SpringContextUtil}
	 */
	@Bean
	public SpringContextUtil springContextUtil(ApplicationContext applicationContext) {
		SpringContextUtil util = new SpringContextUtil();
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
