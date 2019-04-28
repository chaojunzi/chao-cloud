package com.chao.cloud.common.extra.token.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * token注解
 * @功能：
 * @author： 薛超
 * @时间：2019年3月18日
 * @version 1.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FormToken {

    boolean save() default false;

    boolean remove() default false;
}