package com.chao.cloud.common.config.web;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import com.chao.cloud.common.config.required.EnableRequired;
import com.chao.cloud.common.config.valid.EnableValidator;

/**
 * web服务通用
 * @功能：
 * @author： 薛超
 * @时间：2019年3月19日
 * @version 1.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({ //
        AopConfig.class, // controller 拦截和vo转换
        ExceptionController.class, // 全局异常处理
        MyControllerAdvice.class, // 全局控制层异常处理
        WebParameterConfig.class,// web资源-参数处理
})
@EnableRequired // 通用配置
@EnableValidator // 参数校验
public @interface EnableWeb {

}
