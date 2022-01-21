package com.chao.cloud.common.extra.mybatis.dynamic;

import java.lang.reflect.Method;

import org.springframework.aop.ClassFilter;
import org.springframework.aop.support.DynamicMethodMatcherPointcut;

import com.baomidou.mybatisplus.extension.service.IService;

import cn.hutool.core.util.ClassUtil;

/**
 * 方法切入点
 * 
 * @author 薛超
 * @since 2021年11月23日
 * @version 1.0.0
 */
public class DynamicDataSourceMatcherPointcut extends DynamicMethodMatcherPointcut {

	@Override
	public ClassFilter getClassFilter() {
		return clazz -> {
			return ClassUtil.isAssignable(IService.class, clazz);
		};
	}

	@Override
	public boolean matches(Method method, Class<?> targetClass, Object... args) {
		return true;
	}

}
