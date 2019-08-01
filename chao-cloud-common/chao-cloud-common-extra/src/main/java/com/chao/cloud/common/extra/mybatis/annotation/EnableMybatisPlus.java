package com.chao.cloud.common.extra.mybatis.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 数据库插件
 * @author 薛超
 * @since 2019年8月1日
 * @version 1.0.5
 */
@EnableTransactionManagement // 事务
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({ MybatisPlusConfig.class })
public @interface EnableMybatisPlus {

}
