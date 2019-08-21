package com.chao.cloud.common.web.config;

import java.util.Locale;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.hibernate.validator.HibernateValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

/**
 * 全局参数校验配置
 * @author 薛超
 * @since 2019年8月1日
 * @version 1.0.5
 */
@Configuration
public class ValidatorConfig {

	@Bean
	public MethodValidationPostProcessor methodValidationPostProcessor(Validator validator) {
		MethodValidationPostProcessor postProcessor = new MethodValidationPostProcessor();
		/** 设置validator模式为快速失败返回 */
		postProcessor.setValidator(validator);
		return postProcessor;
	}

	@Bean
	public Validator validator() {
		ValidatorFactory validatorFactory = Validation//
				.byProvider(HibernateValidator.class)//
				.configure()//
				.failFast(true)// 快速返回
				.buildValidatorFactory();
		Validator validator = validatorFactory.getValidator();
		return validator;
	}

	/**
	 * 国际化
	 * @return {@link SessionLocaleResolver}
	 */
	@Bean
	public SessionLocaleResolver sessionLocaleResolver() {
		SessionLocaleResolver localeResolver = new SessionLocaleResolver();
		localeResolver.setDefaultLocale(Locale.SIMPLIFIED_CHINESE);
		return localeResolver;
	}
}