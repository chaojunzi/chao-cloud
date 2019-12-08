package com.chao.cloud.common.web.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import com.chao.cloud.common.web.config.SignConfig;

/**
 * 接口参数签名
 * 
 * @author 薛超
 * @since 2019年12月8日
 * @version 1.0.8
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(SignConfig.class)
@Documented
public @interface EnableSign {

}