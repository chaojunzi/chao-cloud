package com.chao.cloud.common.extra.sharding.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

/**
 * ShardingJdbc-分库分表
 * 
 * @author 薛超
 * @since 2020年5月28日
 * @version 1.0.9
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({ ShardingConfig.class })
public @interface EnableShardingJdbc {

}
