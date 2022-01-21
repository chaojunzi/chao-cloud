package com.chao.cloud.common.extra.mybatis.dynamic;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

/**
 * mybatis-plus 动态数据源配置
 * 
 * @author 薛超
 * @since 2020年12月25日
 * @version 1.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(DynamicDataSourceConfig.class)
public @interface EnableDynamicDataSource {

}
