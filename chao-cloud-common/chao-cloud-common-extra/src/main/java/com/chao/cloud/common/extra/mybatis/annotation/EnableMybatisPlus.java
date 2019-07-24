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
 * @功能：
 * @author： 薛超
 * @时间：2019年3月19日
 * @version 1.0.0
 */
@EnableTransactionManagement // 事务
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({ MybatisPlusConfig.class })
public @interface EnableMybatisPlus {

}
