package com.chao.cloud.common.web.crypto;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;

import com.chao.cloud.common.base.BaseHttpServlet;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.ServletUtil;

@ControllerAdvice
@Order(1)
public class SecretRequestAdvice extends RequestBodyAdviceAdapter {

	@Autowired
	private SecretProperties secretProperties;

	public static boolean ignoreSecret() {
		HttpServletRequest request = BaseHttpServlet.getSafeRequest();
		if (request == null) {
			return false;
		}
		String referer = ServletUtil.getHeaderIgnoreCase(request, "Referer");
		if (StrUtil.isBlank(referer)) {
			return false;
		}
		// 构造请求path
		UrlBuilder url = UrlBuilder.of(referer);
		if (StrUtil.endWith(url.getPathStr(), ".html")) {
			return true;
		}
		return false;
	}

	@Override
	public boolean supports(MethodParameter methodParameter, Type targetType,
			Class<? extends HttpMessageConverter<?>> converterType) {
		return supportSecretRequest(methodParameter);
	}

	/**
	 * 是否支持加密消息体
	 *
	 * @param methodParameter methodParameter
	 * @return true/false
	 */
	private boolean supportSecretRequest(MethodParameter methodParameter) {
		boolean ignore = ignoreSecret();
		if (ignore) {
			return false;
		}
		// 判断class是否存在注解
		if (methodParameter.getContainingClass().getAnnotation(secretProperties.getAnnotationClass()) != null) {
			return true;
		}
		// 判断方法是否存在注解
		return methodParameter.getMethodAnnotation(secretProperties.getAnnotationClass()) != null;
	}

	@Override
	public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter parameter, Type targetType,
			Class<? extends HttpMessageConverter<?>> converterType) throws IOException {
		if (!secretProperties.isEnabled()) {
			return inputMessage;
		}
		// 如果支持加密消息，进行消息解密。
		boolean supportSafeMessage = supportSecretRequest(parameter);
		if (!supportSafeMessage) {
			return inputMessage;
		}
		SecretEnum type = secretProperties.getType();
		InputStream in = inputMessage.getBody();
		try {
			String body = IoUtil.readUtf8(in);
			// 此处可能被编码
			body = StrUtil.replace(body, "%3D", "=");
			String httpBody = type.decryptStr(body, secretProperties.getKey());
			// 返回处理后的消息体给messageConvert
			return new SecretHttpMessage(new ByteArrayInputStream(httpBody.getBytes()), inputMessage.getHeaders());
		} finally {
			IoUtil.close(in);
		}
	}

}