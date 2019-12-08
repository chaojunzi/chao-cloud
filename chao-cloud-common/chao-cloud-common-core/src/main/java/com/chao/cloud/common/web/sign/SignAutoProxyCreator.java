package com.chao.cloud.common.web.sign;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator;
import org.springframework.lang.Nullable;

import com.chao.cloud.common.web.annotation.WebConstant;

import cn.hutool.core.util.ReflectUtil;

/**
 * 接口参数签名配置
 * 
 * @author 薛超
 * @since 2019年12月8日
 * @version 1.0.8
 */
@SuppressWarnings("serial")
public class SignAutoProxyCreator extends AbstractAutoProxyCreator {

	@Nullable
	protected Object[] getAdvicesAndAdvisorsForBean(Class<?> beanClass, String beanName,
			@Nullable TargetSource targetSource) {
		// 抽取cglib
		if (beanClass.getName().contains(WebConstant.CGLIB_FLAG)) {
			beanClass = beanClass.getSuperclass();
		}
		if (this.needSign(beanClass)) {
			return PROXY_WITHOUT_ADDITIONAL_INTERCEPTORS;
		}
		return DO_NOT_PROXY;
	}

	/**
	 * 是否需要解密
	 * 
	 * @param beanClass
	 * @return true 为匹配成功
	 */
	private boolean needSign(Class<?> beanClass) {
		// 获取接口中的方法
		Method[] methods = ReflectUtil.getMethods(beanClass, m -> m.getParameterCount() > 0);
		for (Method method : methods) {
			Parameter[] parameters = method.getParameters();
			for (Parameter parameter : parameters) {
				boolean has = this.hasSign(parameter);
				if (has) {
					return true;
				}
			}

		}
		return false;
	}

	/**
	 * 判断注解是否存在
	 * 
	 * @param parameter 参数
	 * @return true 为存在
	 */
	private boolean hasSign(Parameter parameter) {
		// 检查参数
		return parameter.isAnnotationPresent(Sign.class);
	}

}