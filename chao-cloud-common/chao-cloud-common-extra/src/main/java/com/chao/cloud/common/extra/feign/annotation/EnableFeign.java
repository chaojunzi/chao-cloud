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
@Import({ FeignBeanSupportConfig.class, FeignFallbackConfig.class, FeignMultipartSupportConfig.class, HttpPoolConfig.class })
public @interface EnableFeign {

}
