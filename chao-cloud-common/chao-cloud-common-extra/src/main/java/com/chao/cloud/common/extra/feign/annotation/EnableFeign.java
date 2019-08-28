package com.chao.cloud.common.extra.feign.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

/**
 * feign相关基础配置
 * @author 薛超
 * @since 2019年8月1日
 * @version 1.0.5
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(FeignConfig.class)
public @interface EnableFeign {
	/**
	yaml
	hystrix.command.default.execution.timeout.enabled: true
	hystrix.command.default.execution.isolation.thread.timeoutInMilliseconds: ${ribbon.ReadTimeout}
	
	ribbon.ReadTimeout: 120000
	ribbon.ConnectTimeout: ${ribbon.ReadTimeout}
	
	chao:
	  cloud:
		feign:
		  request:
		    read-timeout: 120
		    connect-timeout: 120
		    write-timeout: 120
		    keep-alive-duration: 5 #分钟
		    max-idle-connections: 10
		
		feign:
		  httpclient:
		enabled: false
		  hystrix:
		enabled: true
		  okhttp:
		enabled: true
		  compression:
		request:
		  enabled: true
		  mime-types:
		  - text/xml 
		  - application/xml 
		  - application/json
		  min-request-size: 2048
		response:
		  enabled: true
	 */
}
