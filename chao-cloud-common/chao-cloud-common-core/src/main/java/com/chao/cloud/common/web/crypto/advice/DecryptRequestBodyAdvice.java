package com.chao.cloud.common.web.crypto.advice;

import java.io.IOException;
import java.lang.reflect.Type;

import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;

import com.chao.cloud.common.web.crypto.Crypto;

import lombok.Setter;

/**
 * 请求数据接收处理类
 * 
 * @author 薛超
 * @since 2019年12月3日
 * @version 1.0.8
 */
@ControllerAdvice
@Setter
public class DecryptRequestBodyAdvice implements RequestBodyAdvice {

	private String charset;

	private Crypto crypto;

	@Override
	public boolean supports(MethodParameter methodParameter, Type targetType,
			Class<? extends HttpMessageConverter<?>> converterType) {
		return true;
	}

	@Override
	public Object handleEmptyBody(Object body, HttpInputMessage inputMessage, MethodParameter parameter,
			Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
		return body;
	}

	@Override
	public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter parameter, Type targetType,
			Class<? extends HttpMessageConverter<?>> converterType) throws IOException {
		if (NeedCrypto.needDecrypt(parameter)) {
			return new DecryptHttpInputMessage(inputMessage, charset, crypto);
		}
		return inputMessage;
	}

	@Override
	public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType,
			Class<? extends HttpMessageConverter<?>> converterType) {
		return body;
	}

}