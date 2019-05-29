package com.chao.cloud.admin.sys.log;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * @功能：日志管理
 * @author： 薛超
 * @时间：2019年3月14日
 * @version 1.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AdminLog {

	String STAT_PREFIX = "stat@";

	String value() default "";
}
