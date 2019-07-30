package com.chao.cloud.common.config.auth.config;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

/**
 * 用户接口权限校验 
 * @功能：
 * @author： 薛超
 * @时间：2019年7月30日
 * @version 1.0.3
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(AuthUserConfig.class)
public @interface EnableAuthUser {

}
