package com.chao.cloud.common.support.access.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 接口访问控制
 * @功能：
 * @author： 薛超
 * @时间：2019年4月24日
 * @version 1.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AccessLimit {

    long DEFAULT_TIME = 5 * 60 * 1000;
    int DEFAULT_COUNT = 3;

    /**
     * 拦截持续时间
     */
    long timeout() default DEFAULT_TIME;

    /**
     * 最大访问次数
     */
    int count() default DEFAULT_COUNT;

    /**
     * 是否可用
     */
    boolean enable() default true;

}
