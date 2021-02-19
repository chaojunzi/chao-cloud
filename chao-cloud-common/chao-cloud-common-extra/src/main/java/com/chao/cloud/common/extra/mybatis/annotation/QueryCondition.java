package com.chao.cloud.common.extra.mybatis.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.chao.cloud.common.extra.mybatis.common.FuncExps;
import com.chao.cloud.common.extra.mybatis.common.SqlTemplate;

/**
 * mybatis-plus 查询条件注解
 * 
 * @author 薛超
 * @since 2021年1月11日
 * @version 1.0.0
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface QueryCondition {
	/**
	 * sql 模板<br>
	 * 
	 * @return {@link SqlTemplate}
	 */
	SqlTemplate value();

	/**
	 * 方法表达式
	 * 
	 * @return {@link FuncExps}
	 */
	Class<? extends FuncExps> funcClass() default FuncExps.class;

}