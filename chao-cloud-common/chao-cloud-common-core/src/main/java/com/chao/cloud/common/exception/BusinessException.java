package com.chao.cloud.common.exception;

/**
 * 自定义业务异常处理类 
 * @author 薛超
 * @since 2019年8月1日
 * @version 1.0.5
 */
public class BusinessException extends RuntimeException {
	private static final long serialVersionUID = 3152616724785436891L;

	public BusinessException(String frdMessage) {
		super(frdMessage);
	}

	public BusinessException(Throwable throwable) {
		super(throwable);
	}

	public BusinessException(Throwable throwable, String frdMessage) {
		super(frdMessage, throwable);
	}

}