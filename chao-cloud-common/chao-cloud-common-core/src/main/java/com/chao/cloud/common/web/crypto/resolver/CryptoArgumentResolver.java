package com.chao.cloud.common.web.crypto.resolver;

import java.util.Iterator;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.chao.cloud.common.annotation.ArgumentAnnotation;
import com.chao.cloud.common.web.crypto.annotaion.Crypto;

import cn.hutool.core.util.ReflectUtil;

/**
 * 解析数据
 * 
 * @author 薛超
 * @since 2019年12月3日
 * @version 1.0.8
 */
@ArgumentAnnotation
public class CryptoArgumentResolver implements HandlerMethodArgumentResolver {

	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.hasParameterAnnotation(Crypto.class);
	}

	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
		return handleParameterNames(parameter, webRequest);
	}

	private Object handleParameterNames(MethodParameter parameter, NativeWebRequest webRequest) {
		// 实例化对象
		Object obj = ReflectUtil.newInstanceIfPossible(parameter.getParameterType());
		BeanWrapper wrapper = PropertyAccessorFactory.forBeanPropertyAccess(obj);
		Iterator<String> paramNames = webRequest.getParameterNames();
		while (paramNames.hasNext()) {
			String paramName = paramNames.next();
			Object o = webRequest.getParameter(paramName);
			wrapper.setPropertyValue(paramName, o);
		}
		return obj;
	}

}