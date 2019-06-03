package com.chao.cloud.admin.sys.log;

import java.lang.reflect.Method;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.chao.cloud.admin.sys.dal.entity.SysLog;
import com.chao.cloud.admin.sys.domain.dto.UserDTO;
import com.chao.cloud.admin.sys.service.SysLogService;
import com.chao.cloud.admin.sys.shiro.ShiroUtils;
import com.chao.cloud.common.base.BaseHttpServlet;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 日志代理
 * @功能：
 * @author： 薛超
 * @时间： 2019年5月29日
 * @version 1.0.0
 */
@Aspect
@Slf4j
@Component
public class LogProxy implements BaseHttpServlet {

	private final int maxLength = 150;

	@Autowired
	private SysLogService sysLogService;

	@Around("@annotation(com.chao.cloud.admin.sys.log.AdminLog)")
	public Object around(ProceedingJoinPoint point) throws Throwable {
		long before = System.nanoTime();
		// 执行方法
		Object result = point.proceed();
		// 执行时长(微秒)
		long time = (System.nanoTime() - before) / 1000;
		// 异步保存日志
		saveLog(point, time);
		return result;
	}

	void saveLog(ProceedingJoinPoint joinPoint, long time) throws InterruptedException {
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		Method method = signature.getMethod();
		SysLog sysLog = new SysLog();
		AdminLog syslog = method.getAnnotation(AdminLog.class);
		if (syslog != null) {
			// 注解上的描述
			sysLog.setOperation(syslog.value());
		}
		// 请求的方法名
		String className = joinPoint.getTarget().getClass().getName();
		String methodName = signature.getName();
		sysLog.setMethod(className + "." + methodName + "()");
		// 请求的参数
		Object[] args = joinPoint.getArgs();
		try {
			String params = JSONUtil.toJsonStr(args);
			sysLog.setParams(StrUtil.brief(params, maxLength));
		} catch (Exception e) {
			log.error("[日志异常:{}]", ExceptionUtil.getMessage(e));
		}
		// 获取request
		HttpServletRequest request = getRequest();
		// 设置IP地址
		String clientIP = ServletUtil.getClientIP(request);
		// requestut
		sysLog.setIp(clientIP);
		// 用户名
		UserDTO currUser = ShiroUtils.getUser();
		if (null == currUser) {
			sysLog.setUserId(-1);
			sysLog.setUsername("获取用户信息为空");
		} else {
			sysLog.setUserId(ShiroUtils.getUserId());
			sysLog.setUsername(ShiroUtils.getUser().getUsername());
		}
		sysLog.setTime((int) time);
		// 系统当前时间
		Date date = new Date();
		sysLog.setGmtCreate(date);
		// 保存系统日志
		sysLogService.save(sysLog);
	}
}
