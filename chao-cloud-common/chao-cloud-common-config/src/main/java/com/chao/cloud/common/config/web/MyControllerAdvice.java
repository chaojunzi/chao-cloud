package com.chao.cloud.common.config.web;

import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.chao.cloud.common.base.BaseLogger;
import com.chao.cloud.common.constants.ResultCodeEnum;
import com.chao.cloud.common.entity.Response;
import com.chao.cloud.common.entity.ResponseResult;
import com.chao.cloud.common.exception.BusinessException;

/**
 * controller 增强器
 * 
 * @author 薛超 功能： 时间：2018年10月15日
 * @version 1.0
 */
@RestControllerAdvice
public class MyControllerAdvice implements BaseLogger {

    /**
     * 全局异常捕捉处理
     * 
     * @param ex
     * @return
     */
    @ExceptionHandler(value = { Throwable.class, Exception.class, BusinessException.class })
    public Response<String> errorHandler(Exception ex) {
        return ResponseResult.getResponseCodeAndMsg(ResultCodeEnum.CODE_500.code(), ex.getMessage());
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