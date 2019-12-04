package com.chao.cloud.common.web.crypto.advice;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.chao.cloud.common.entity.Response;
import com.chao.cloud.common.web.crypto.Crypto;

import cn.hutool.core.lang.Console;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.json.JSONUtil;
import lombok.Setter;

/**
 * 请求响应处理类
 * 
 * @author 薛超
 * @since 2019年12月3日
 * @version 1.0.8
 */
@ControllerAdvice
@Setter
public class EncryptResponseBodyAdvice implements ResponseBodyAdvice<Object> {

	private String charset;

	private Crypto crypto;

	@Override
	public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
		return true;
	}

	@Override
	public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
			Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request,
			ServerHttpResponse response) {
		boolean encrypt = NeedCrypto.needEncrypt(returnType);
		if (!encrypt) {
			return body;
		}

		if (!(body instanceof Response)) {
			return body;
		}

		// 只针对Response的data进行加密
		Response r = (Response) body;
		Object data = r.getBody();
		if (null == data) {
			return body;
		}

		String xx;
		Class<?> dataClass = data.getClass();
		if (ClassUtil.isSimpleValueType(dataClass)) {
			xx = String.valueOf(data);
		} else {
			// JavaBean、Map、List等先序列化
			xx = JSONUtil.toJsonStr(data);
		}
		// 请自行使用加密方法的
		Console.log("加密返回值:{}" + xx);
		// r.setBody(crypto.encrypt(xx, charset));
		return r;
	}
}
