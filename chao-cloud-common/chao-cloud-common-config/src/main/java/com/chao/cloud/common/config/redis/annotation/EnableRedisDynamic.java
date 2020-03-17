package com.chao.cloud.common.config.redis.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

/**
 * redis 动态数据源
 * 
 * @author 薛超
 * @since 2020年3月17日
 * @version 1.0.9
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(RedisDynamicConfig.class)
public @interface EnableRedisDynamic {

}
