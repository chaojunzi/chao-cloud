package com.chao.cloud.common.web.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import com.chao.cloud.common.web.config.ControllerConfig;
import com.chao.cloud.common.web.config.SecretConfig;
import com.chao.cloud.common.web.config.VersionConfig;
import com.chao.cloud.common.web.config.WebMvcConfig;

/**
 * web服务通用配置
 * 
 * @author 薛超
 * @since 2019年8月1日
 * @version 1.0.5
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({ //
		ControllerConfig.class, // controller 拦截和vo转换
		WebMvcConfig.class, // web资源-参数处理
		SecretConfig.class, // 参数加密
		VersionConfig.class,// 版本号
})
@EnableSpring // 核心配置
@EnableValidator // 参数校验
public @interface EnableWeb {

}
