package com.chao.cloud.common.web.crypto;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 解密注解<br>
 * 此注解只能应用于String<br>
 * 
 * @author 薛超
 * @since 2019年12月3日
 * @version 1.0.8
 */
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Crypto {
	/**
	 * 解密类型
	 * 
	 * @return {@link CryptoTypeEnum}
	 */
	CryptoTypeEnum value() default CryptoTypeEnum.RSA;
}