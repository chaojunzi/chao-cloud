package com.chao.cloud.common.support.feign.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * @功能：回退拦截
 * @author： 薛超
 * @时间：2019年2月28日
 * @version 2.0
 */
@Target(ElementType.METHOD)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface FeignFallback {

}
