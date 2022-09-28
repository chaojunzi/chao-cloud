package com.chao.cloud.common.extra.mybatis.config;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

/**
 * 动态数据源
 * 
 * @author 薛超
 * @since 2022年4月19日
 * @version 1.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({ SingleDataSourceConfig.class, DynamicDataSourceConfig.class })
@Documented
public @interface EnableDynamicMybatis {

}
