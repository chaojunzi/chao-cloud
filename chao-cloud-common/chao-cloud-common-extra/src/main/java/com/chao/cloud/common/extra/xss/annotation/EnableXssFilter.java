package com.chao.cloud.common.extra.xss.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

/**
 * xss 跨站点脚本编制
 * 
 * @author 薛超
 * @since 2019年11月12日
 * @version 1.0.8
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(XssFilterConfig.class)
@Documented
public @interface EnableXssFilter {

}