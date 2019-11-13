package com.chao.cloud.common.extra.xss.annotation;

import javax.servlet.Filter;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.chao.cloud.common.extra.xss.filter.XSSFilter;

import cn.hutool.core.util.StrUtil;
import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "chao.cloud.xss")
@Data
public class XssFilterConfig {

	private String urlPatterns = "/*";
	private String filterName;
	private Integer order = 0;

	@Bean
	@ConditionalOnMissingBean(value = FilterRegistrationBean.class)
	public FilterRegistrationBean<Filter> configFilter() {
		return new FilterRegistrationBean<Filter>();
	}

	@Bean
	public XSSFilter xssFilter(FilterRegistrationBean<Filter> filterRegistrationBean) {
		XSSFilter xssFilter = new XSSFilter();
		filterRegistrationBean.setFilter(xssFilter);
		filterRegistrationBean.addUrlPatterns(urlPatterns);
		if (StrUtil.isBlank(filterName)) {
			filterName = xssFilter.getClass().getSimpleName();
		}
		filterRegistrationBean.setName(filterName);
		// spring boot会按照order值的大小，从小到大的顺序来依次过滤
		filterRegistrationBean.setOrder(order);
		return xssFilter;
	}

}
