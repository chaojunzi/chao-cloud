package com.chao.cloud.common.config.cron.config;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

/**
 * 定时器
 * @author 薛超
 * @since 2019年8月21日
 * @version 1.0.7
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(CronConfig.class)
public @interface EnableCron {

}
