package com.chao.cloud.common.extra.xss.annotation;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import com.chao.cloud.common.extra.xss.filter.XSSFilter;

@Configuration
@ServletComponentScan
public class XssFilterConfig {
	@Bean
	@Order(2)
	// spring boot会按照order值的大小，从小到大的顺序来依次过滤
	public FilterRegistrationBean configFilter() {
		FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
		filterRegistrationBean.setFilter(new XSSFilter());
		filterRegistrationBean.addUrlPatterns("/*");
		filterRegistrationBean.setName("sessionFilter");
		// filterRegistrationBean.setOrder(2);
		return filterRegistrationBean;
	}

	@Bean
	public XSSFilter xssFilter() {
		return new XSSFilter();
	}

}
