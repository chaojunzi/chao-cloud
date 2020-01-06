package com.chao.cloud.common.web.controller;

import java.lang.reflect.Method;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.chao.cloud.common.annotation.ExcludeAnnotation;
import com.chao.cloud.common.entity.Response;

/**
 * 返回值统一处理
 * 
 * @author 薛超
 * @since 2020年1月6日
 * @version 1.0.9
 */
@RestControllerAdvice
public class ResponseBodyHandler implements ResponseBodyAdvice<Object> {

	@Override
	public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
		Method method = returnType.getMethod();
		Class<?> type = method.getReturnType();
		return !method.isAnnotationPresent(ExcludeAnnotation.class)// 不包含此注解
				&& type != Response.class// 非Response类型
				&& type != Void.TYPE;// 非Void
	}

	@Override
	public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
			Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request,
			ServerHttpResponse response) {
		// 修改返回值类型
		return Response.ok(body);
	}

}
