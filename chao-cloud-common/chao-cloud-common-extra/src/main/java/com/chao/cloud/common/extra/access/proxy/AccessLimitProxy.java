package com.chao.cloud.common.extra.access.proxy;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.InitializingBean;

import com.chao.cloud.common.base.BaseProxy;
import com.chao.cloud.common.exception.BusinessException;
import com.chao.cloud.common.extra.access.annotation.AccessLimit;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.ServletUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 接口访问限制-ip+port-controller层拦截
 * @author 薛超
 * @since 2019年8月1日
 * @version 1.0.5
 */
@Aspect
@Slf4j
public class AccessLimitProxy implements BaseProxy, InitializingBean {
	/**
	 * 定时缓存 
	 */
	private final TimedCache<String, Integer> accessLimitCache = CacheUtil.newTimedCache(AccessLimit.DEFAULT_TIME);

	// 环绕拦截
	@Around(value = "@annotation(org.springframework.web.bind.annotation.RequestMapping)")
	public Object around(ProceedingJoinPoint pdj) throws Throwable {
		// 获取method 中的注解
		Method method = this.getMethod(pdj);
		AccessLimit access = method.getAnnotation(AccessLimit.class);
		//
		HttpServletRequest request = this.getRequest();
		// 获取key=ip+port+url
		String ip = ServletUtil.getClientIP(request, null);
		String key = StrUtil.format("{}:{}@{}", ip, request.getRemotePort(), request.getRequestURI());

		Integer val = null;
		//
		long timeout = ObjectUtil.isNull(access) ? AccessLimit.DEFAULT_TIME : access.timeout();
		//
		boolean check = ObjectUtil.isNull(access) || access.enable();
		if (check) { // 开始校验
			val = accessLimitCache.get(key, false);
			log.info("[access:key={},count={}]", key, ObjectUtil.isNull(val) ? 0 : val);
			int count = ObjectUtil.isNull(access) ? AccessLimit.DEFAULT_COUNT : access.count();
			if (val != null && val >= count) {
				throw new BusinessException("HTTP请求超出设定的限制");
			}
		}
		// 执行
		try {
			return pdj.proceed();
		} catch (Throwable e) {
			if (check) {
				// 异常记录+1
				accessLimitCache.put(key, ObjectUtil.isNull(val) ? 1 : val + 1, timeout);
			}
			throw e;
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// 初始化设置扫描时间
		accessLimitCache.schedulePrune(AccessLimit.DEFAULT_TIME);

	}

}