package com.chao.cloud.common.extra.sharding.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.chao.cloud.common.extra.sharding.constant.ColumnType;

/**
 * 分片类型转化注解
 * 
 * @author 薛超
 * @since 2020年12月31日
 * @version 1.0.9
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ShardingColumn {
	/**
	 * shardingCode 处理
	 */
	boolean shardingCode() default true;

	/**
	 * 字段 默认值
	 * 
	 */
	boolean defaultSet() default true;

	/**
	 * 校验部门是否存在
	 */
	boolean validateOrgCode() default false;

	/**
	 * 字段类型
	 */
	ColumnType type() default ColumnType.JSON;

}