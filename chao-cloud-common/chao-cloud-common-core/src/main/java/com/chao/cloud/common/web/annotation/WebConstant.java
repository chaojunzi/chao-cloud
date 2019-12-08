package com.chao.cloud.common.web.annotation;

/**
 * web常量
 * 
 * @author 薛超
 * @since 2019年12月6日
 * @version 1.0.8
 */
public interface WebConstant {
	/**
	 * cglib标志
	 */
	String CGLIB_FLAG = "$$EnhancerBySpringCGLIB$$";
	/**
	 * 控制层拦截器名称
	 */
	String CONTROLLER_INTERCEPTOR = "controllerInterceptor";
	/**
	 * 解密接口拦截器名称
	 */
	String CRYPTO_INTERCEPTOR = "cryptoInterceptor";
	/**
	 * 接口签名拦截器名称
	 */
	String SIGN_INTERCEPTOR = "signInterceptor";
	/**
	 * Controller执行顺序
	 */
	Integer CONTROLLER_ORDER = Integer.MAX_VALUE;
	/**
	 * Crypto执行顺序
	 */
	Integer CRYPTO_ORDER = 1;
	/**
	 * Sign执行顺序
	 */
	Integer SIGN_ORDER = CRYPTO_ORDER + 10;
}
