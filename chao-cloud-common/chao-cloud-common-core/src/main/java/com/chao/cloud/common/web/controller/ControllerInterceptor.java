package com.chao.cloud.common.web.controller;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.Ordered;

import com.chao.cloud.common.base.BaseHttpServlet;
import com.chao.cloud.common.constant.ExceptionConstant;
import com.chao.cloud.common.constant.ResultCodeEnum;
import com.chao.cloud.common.entity.Response;
import com.chao.cloud.common.web.crypto.SecretProperties;
import com.chao.cloud.common.web.crypto.SecretRequestAdvice;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.json.JSONUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class ControllerInterceptor implements MethodInterceptor, BaseHttpServlet, Ordered {
	private int order;
	final String concat = StrUtil.repeat(StrUtil.DASHED, 30);

	@Autowired
	private SecretProperties secretProperties;

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		Method method = invocation.getMethod();
		if (invocation.getThis() instanceof ErrorController) {
			return invocation.proceed();
		}
		HttpServletRequest request = getRequest();
		if (request == null) {
			// 非http请求
			return invocation.proceed();
		}
		Class<?> returnType = method.getReturnType();
		Object obj = null;
		TimeInterval interval = new TimeInterval();
		interval.start();
		try {
			log.info("【请求路径】: [{}] - [{}] - [{}]", ServletUtil.getClientIP(request), request.getMethod(),
					request.getRequestURI());// 请求路径
			Object[] args = invocation.getArguments();
			// 参数打印
			if (ArrayUtil.isNotEmpty(args)) {
				String[] argNames = getParameterNames(method);
				Object[] values = new Object[args.length];
				StringBuilder paramBuilder = StrUtil.builder();

				String splitLine = StrUtil.format("{} param {}", concat, concat);
				paramBuilder.append("【请求参数】: \n" + splitLine);
				CollUtil.forEach(CollUtil.toList(args), (arg, i) -> {
					paramBuilder.append(StrUtil.format("\n[{}]: {}", argNames[i]));
					values[i] = this.formatArg(arg);
				});
				paramBuilder.append("\n" + splitLine);
				log.info(paramBuilder.toString(), values);
			}
			// 执行方法
			obj = invocation.proceed();
		} catch (Exception e) {
			// 针对controller层抛出的异常做处理
			String error = e.getMessage();
			if (e instanceof NullPointerException) {// 空指针
				StackTraceElement ste = ArrayUtil.firstNonNull(e.getStackTrace());
				error = StrUtil.format("【空指针异常】: {}.{}({}:{})", //
						ste.getClassName(), //
						ste.getMethodName(), //
						ste.getFileName(), //
						ste.getLineNumber());
			}
			if (StrUtil.isBlank(error)) {
				error = ExceptionConstant.ERROR;
			}
			// 判断返回值类型
			if (returnType == Response.class || returnType == Void.class) {
				obj = Response.result(ResultCodeEnum.CODE_500.code(), error);
			} else {
				throw e;
			}
		} finally {
			// 返回值
			if (obj != null) {
				log.info("【返回值】: {}", JSONUtil.toJsonStr(obj));
			}
			// 计算方法执行时间
			log.info("【执行时间】: [{}]", interval.intervalPretty());
		}
		// 判断是否需要加密
		boolean boo = !SecretRequestAdvice.ignoreSecret() //
				&& secretProperties.isEnabled()//
				&& method.getAnnotation(secretProperties.getAnnotationClass()) != null;
		if (boo) {
			obj = secretProperties.getType().encryptBase64(JSONUtil.toJsonStr(obj), secretProperties.getKey());
		}
		// 不加密
		if (!boo && obj instanceof String) {
			String str = (String) obj;
			boolean isXml = StrUtil.startWith(str, "<?xml");
			if (isXml) {
				obj = StrUtil.removePrefix(str, "<?xml version=\"1.0\" encoding=\"gbk\"?>");
			}
		}
		return obj;
	}

	private String formatArg(Object arg) {
		if (arg == null) {
			return StrUtil.EMPTY;
		}
		Class<? extends Object> type = arg.getClass();
		if (ClassUtil.isSimpleTypeOrArray(type)) {
			if (type.isArray()) {
				return ArrayUtil.toString(arg);
			}
			return StrUtil.toString(arg);
		}
		if (isInclude(arg)) {
			return ClassUtil.getClassName(type, true);
		}
		return JSONUtil.toJsonStr(arg);
	}

	String[] getParameterNames(Method method) {
		LocalVariableTableParameterNameDiscoverer u = new LocalVariableTableParameterNameDiscoverer();
		return u.getParameterNames(method);
	}

}
