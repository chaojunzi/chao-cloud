package com.chao.cloud.common.web.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import com.chao.cloud.common.exception.ExceptionControllerAdvice;
import com.chao.cloud.common.exception.ExceptionControllerError;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({ //
		ExceptionControllerError.class, // 全局异常处理
		ExceptionControllerAdvice.class, // 全局控制层异常处理
})
public @interface EnableGlobalException {

}
