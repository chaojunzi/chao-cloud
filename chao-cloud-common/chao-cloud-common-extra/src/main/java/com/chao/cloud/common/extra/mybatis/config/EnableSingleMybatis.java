package com.chao.cloud.common.extra.mybatis.config;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

/**
 * 自定义mybatis 插件
 * 
 * @author 薛超
 * @since 2020年12月25日
 * @version 1.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(SingleDataSourceConfig.class)
public @interface EnableSingleMybatis {

}
