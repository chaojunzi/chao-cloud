package com.chao.cloud.common.extra.access.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 接口访问控制
 * @author 薛超
 * @since 2019年8月1日
 * @version 1.0.5
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AccessLimit {

	long DEFAULT_TIME = 5 * 60 * 1000;
	int DEFAULT_COUNT = 3;

	/**
	 * 拦截持续时间
	 * @return long
	 */
	long timeout() default DEFAULT_TIME;

	/**
	 * 最大访问次数
	 * @return int
	 */
	int count() default DEFAULT_COUNT;

	/**
	 * 是否可用
	 * @return boolean
	 */
	boolean enable() default true;

}
