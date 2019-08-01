package com.chao.cloud.common.config.auth.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.chao.cloud.common.constant.ResultCodeEnum;

/**
 * 权限注解
 * @author 薛超
 * @since 2019年8月1日
 * @version 1.0.5
 */
@Target(ElementType.METHOD)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface Permission {
	/**
	 * 权限列表
	 * @return int[]
	 */
	int[] hasPerm();

	/**
	 * 无权限码
	 * @return {@link ResultCodeEnum}
	 */
	ResultCodeEnum retCode() default ResultCodeEnum.CODE_403;

	/**
	 * 返回值信息
	 * @return String
	 */
	String retMsg() default "权限不足";

}
