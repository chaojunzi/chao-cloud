package com.chao.cloud.common.web.convert;

import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import com.chao.cloud.common.base.BaseHttpServlet;
import com.chao.cloud.common.constant.ExceptionConstant;
import com.chao.cloud.common.constant.ResultCodeEnum;
import com.chao.cloud.common.entity.Response;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConvertInterceptor implements MethodInterceptor, BaseHttpServlet {

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		Object obj = null;
		long before = System.nanoTime();
		Method method = invocation.getMethod();
		Class<?> returnType = method.getReturnType();
		try {
			HttpServletRequest request = getRequest();
			log.info("start-----------------------------------------------------------------------------");
			log.info("【请求方法】[{}]", method);
			Object[] objects = invocation.getArguments();
			log.info("【返回类型】[{}]", returnType);
			// 参数
			List<Object> list = Stream.of(objects).filter(o -> !isInclude(o)).collect(Collectors.toList());
			// json格式化
			String params = JSONUtil.toJsonStr(list);
			log.info("【请求路径】[{}]:[{}]】", request.getMethod(), request.getRequestURI());// 请求路径
			log.info("【请求参数】{}", params);
			// 判断是否执行失误
			String exit = (String) request.getAttribute("exit");
			log.info("quit" + exit);
			if (!"!".equals(exit)) {
				// 执行方法
				obj = invocation.proceed();
			}
		} catch (Throwable e) {
			log.error("[error]{}", e);
			// 针对controller层抛出的异常做处理
			String error = e.getMessage();
			if (StrUtil.isBlank(error)) {
				error = ExceptionConstant.ERROR;
			}
			// 判断返回值类型
			if (returnType == Response.class) {
				obj = Response.result(ResultCodeEnum.CODE_500.code(), error);
			} else {
				throw e;
			}
		}
		long after = System.nanoTime();
		// 计算方法执行时间
		log.info("【执行时间】[{}s]]", (after - before) / 1000000000.0);
		log.info("end------------------------------------------------------------------------------");
		return obj;
	}

	public boolean isInclude(Object object) {
		return (object instanceof HttpServletRequest//
				|| object instanceof HttpServletResponse//
				|| object instanceof HttpSession//
		);
	}

}
