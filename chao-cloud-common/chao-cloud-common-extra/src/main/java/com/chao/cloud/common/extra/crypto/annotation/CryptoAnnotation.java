package com.chao.cloud.common.extra.crypto.annotation;

/**
 * 类型注解
 * 
 * @author 薛超
 * @since 2019年11月28日
 * @version 1.0.8
 */
public @interface CryptoAnnotation {
	/**
	 * 解密类型
	 * 
	 * @return {@link CryptoTypeEnum}
	 */
	CryptoTypeEnum type() default CryptoTypeEnum.RSA;

}
