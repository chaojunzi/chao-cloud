package com.chao.cloud.common.extra.tx.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import io.seata.config.ConfigType;

/**
 * 分布式事务-seata
 * @author 薛超
 * @since 2019年8月27日
 * @version 1.0.7
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(TxSeataImportSelector.class)
public @interface EnableTxSeata {

	String TX_SEATA_PREFIX = "chao.cloud.tx.seata";

	/**
	 * 目前只支持Nacos
	 * @return {@link ConfigType}
	 */
	ConfigType value() default ConfigType.Nacos;

}
