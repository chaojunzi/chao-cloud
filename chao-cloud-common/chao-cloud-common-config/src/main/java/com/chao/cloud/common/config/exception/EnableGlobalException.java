package com.chao.cloud.common.config.exception;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({ //
		ExceptionControllerError.class, // 全局异常处理
		ExceptionControllerAdvice.class, // 全局控制层异常处理
})
public @interface EnableGlobalException {

}
