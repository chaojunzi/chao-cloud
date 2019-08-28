package com.chao.cloud.common.extra.tx.proxy;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.util.StringUtils;

import com.chao.cloud.common.base.BaseProxy;

import io.seata.core.context.RootContext;
import lombok.extern.slf4j.Slf4j;

/**
 * 分布式事务Feign传递XID
 * @author 薛超
 * @since 2019年8月27日
 * @version 1.0.7
 */
@Aspect
@Slf4j
public class TxSeataFeignProxy implements BaseProxy {

	// 环绕拦截
	@Around(value = "@annotation(org.springframework.web.bind.annotation.RequestMapping)")
	public Object around(ProceedingJoinPoint pdj) throws Throwable {
		Method method = getMethod(pdj);
		Class<?> declaringClass = method.getDeclaringClass();
		// 是否为内部Feign调用
		if (declaringClass.isAnnotationPresent(FeignClient.class)) {
			return pdj.proceed();
		}
		// 设置XID
		HttpServletRequest request = getRequest();
		try {
			this.before(request);
			// 执行
			return pdj.proceed();
		} finally {
			this.after(request);
		}
	}

	private void before(HttpServletRequest request) {
		String xid = RootContext.getXID();
		String rpcXid = request.getHeader(RootContext.KEY_XID);
		log.info("Before-xid in RootContext {} xid in RpcContext {}", xid, rpcXid);
		if (xid == null && rpcXid != null) {
			RootContext.bind(rpcXid);
			log.info("Before-bind {} to RootContext", rpcXid);
		}
	}

	private void after(HttpServletRequest request) {
		String rpcXid = request.getHeader(RootContext.KEY_XID);
		if (StringUtils.isEmpty(rpcXid)) {
			return;
		}
		String unbindXid = RootContext.unbind();
		log.info("After-unbind {} from RootContext", unbindXid);
		if (!rpcXid.equalsIgnoreCase(unbindXid)) {
			log.warn("After-xid in change during RPC from {} to {}", rpcXid, unbindXid);
			if (unbindXid != null) {
				RootContext.bind(unbindXid);
				log.warn("After-bind {} back to RootContext", unbindXid);
			}
		}
	}

}
