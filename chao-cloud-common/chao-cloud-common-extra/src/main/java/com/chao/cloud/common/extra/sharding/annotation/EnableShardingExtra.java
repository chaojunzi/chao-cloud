package com.chao.cloud.common.extra.sharding.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import cn.hutool.extra.spring.EnableSpringUtil;

/**
 * 自定义分库分表配置-扩展
 * 
 * @author 薛超
 * @since 2020年12月31日
 * @version 1.0.9
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@EnableSpringUtil
@Import({ ShardingConfig.class, ShardingExtraConfig.class })
public @interface EnableShardingExtra {

}
