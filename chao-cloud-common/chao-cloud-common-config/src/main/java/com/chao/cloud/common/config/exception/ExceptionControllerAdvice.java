package com.chao.cloud.common.config.exception;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.chao.cloud.common.base.BaseHttpServlet;
import com.chao.cloud.common.constants.ResultCodeEnum;
import com.chao.cloud.common.entity.Response;
import com.chao.cloud.common.entity.ResponseResult;
import com.chao.cloud.common.exception.BusinessException;

import lombok.extern.slf4j.Slf4j;

/**
 * controller 增强器
 * 
 * @author 薛超 功能： 时间：2018年10月15日
 * @version 1.0
 */
@RestControllerAdvice
@Slf4j
public class ExceptionControllerAdvice implements BaseHttpServlet {

	/**
	 * 全局异常捕捉处理
	 * 
	 * @param ex
	 * @return
	 */
	@ExceptionHandler(value = { Throwable.class, Exception.class, BusinessException.class })
	public Response<String> errorHandler(Exception ex) {
		log.info("AdviceError={}", ex.getMessage());
		// 判断是否为 ajax 请求
		HttpServletRequest request = getRequest();
		if (requestIsAjax(request)) {
			return ResponseResult.getResponseCodeAndMsg(ResultCodeEnum.CODE_500.code(), ex.getMessage());
		}
		// 返回错误页面
		HttpServletResponse response = getResponse();
		ErrorUtil.writeErrorHtml(response, ex.getClass());
		return null;
	}

	@ExceptionHandler(value = { BindException.class })
	public Response<String> errorHandler(BindException ex) {
		FieldError fieldError = ex.getFieldError();
		StringBuilder sb = new StringBuilder();
		sb.append(fieldError.getField()).append("=[").append(fieldError.getRejectedValue()).append("]")
				.append(fieldError.getDefaultMessage());
		return ResponseResult.getResponseCodeAndMsg(ResultCodeEnum.CODE_500.code(), sb.toString());
	}

}