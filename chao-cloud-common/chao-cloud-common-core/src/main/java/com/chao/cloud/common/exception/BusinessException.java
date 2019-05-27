package com.chao.cloud.common.exception;

/**
 * 自定义业务异常处理类 
 * @功能：
 * @author： 薛超
 * @时间： 2019年5月27日
 * @version 1.0.0
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