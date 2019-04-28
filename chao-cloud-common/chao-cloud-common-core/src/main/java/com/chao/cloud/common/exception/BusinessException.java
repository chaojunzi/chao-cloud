package com.chao.cloud.common.exception;

/**
 * 自定义业务异常处理类 友好提示
 * 
 * @author Andy Chan
 * 
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