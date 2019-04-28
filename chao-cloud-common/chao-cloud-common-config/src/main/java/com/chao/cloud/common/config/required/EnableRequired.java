package com.chao.cloud.common.config.required;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

/**
 * 全局的application配置和异步线程池-这是必须的
 * @功能：
 * @author： 薛超
 * @时间：2019年3月19日
 * @version 1.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({ ApplicationBeanConfig.class, ThreadConfig.class })
public @interface EnableRequired {

}
