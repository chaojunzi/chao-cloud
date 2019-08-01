package com.chao.cloud.common.extra.feign.proxy;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import com.chao.cloud.common.base.BaseProxy;
import com.chao.cloud.common.constant.ResultCodeEnum;
import com.chao.cloud.common.entity.Response;
import com.chao.cloud.common.exception.BusinessException;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Slf4j
public class FeignFallbackProxy implements BaseProxy {
	/**
	 * feign异步调用拦截
	 */
	@Around(value = "@annotation(com.chao.cloud.common.extra.feign.annotation.FeignFallback)")
	public Object around(ProceedingJoinPoint pdj) throws Exception {
		Object obj = null;
		String serverName = "undefined";
		try {
			Method method = getMethod(pdj);
			Object target = pdj.getTarget();
			String targetString = target.toString();
			serverName = targetString.split(",")[1];
			log.info("[\nFeignClient={},\nMethod={},\nParam={}]", targetString, method, pdj.getArgs());
			obj = pdj.proceed();
		} catch (Throwable e) {
			throw new BusinessException("[Server:" + serverName + "]" + e.getMessage());
		}
		if (obj instanceof Response) {
			Response<?> result = (Response<?>) obj;
			if (!ResultCodeEnum.CODE_200.code().equals(result.getRetCode())) {
				throw new BusinessException(result.getRetMsg());
			}
		}
		return obj;
	}

}
