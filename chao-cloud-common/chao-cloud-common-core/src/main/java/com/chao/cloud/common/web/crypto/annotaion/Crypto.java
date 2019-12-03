package com.chao.cloud.common.web.crypto.annotaion;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 解密注解
 * 
 * @author 薛超
 * @since 2019年11月28日
 * @version 1.0.8
 */
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface Crypto {
	/**
	 * 解密类型
	 * 
	 * @return {@link CryptoTypeEnum}
	 */
	CryptoTypeEnum type() default CryptoTypeEnum.RSA;

}
