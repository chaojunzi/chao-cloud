package com.chao.cloud.common.support.token.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

/**
 * 防止表单重复提交
 * @功能：
 * @author： 薛超
 * @时间：2019年3月18日
 * @version 1.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(FormTokenConfiguration.class)
@Documented
public @interface EnableFormToken {

}
