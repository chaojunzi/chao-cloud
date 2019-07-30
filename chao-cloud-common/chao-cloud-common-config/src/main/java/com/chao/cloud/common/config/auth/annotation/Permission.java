package com.chao.cloud.common.config.auth.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.chao.cloud.common.constants.ResultCodeEnum;

/**
 * 
 * @功能： 权限注解
 * @author： 薛超
 * @时间： 2019年7月30日
 * @version 1.0.0
 */
@Target(ElementType.METHOD)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface Permission {
	/**
	 * 权限列表
	 * 
	 * @return
	 */
	int[] hasPerm();

	/**
	 * 无权限码
	 */
	ResultCodeEnum retCode() default ResultCodeEnum.CODE_403;

	/**
	 * 返回值注释
	 */
	String retMsg() default "权限不足";

}
