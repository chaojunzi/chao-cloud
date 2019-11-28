package com.chao.cloud.common.extra.crypto.proxy;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import com.chao.cloud.common.base.BaseProxy;

import lombok.extern.slf4j.Slf4j;

/**
 * 接口参数解析-> 必须放在参数解析前
 * 
 * @author 薛超
 * @since 2019年11月28日
 * @version 1.0.8
 */
@Aspect
@Slf4j
public class CryptoProxy implements BaseProxy {

	// 环绕拦截
	@Around(value = "@annotation(org.springframework.web.bind.annotation.RequestMapping)")
	public Object around(ProceedingJoinPoint pdj) throws Throwable {
		// 获取method 中的注解
		Method method = this.getMethod(pdj);
		return pdj.proceed();

	}

}