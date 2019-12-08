package com.chao.cloud.common.web.sign;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 接口参数签名<br>
 * 只处理简单值类型<br>
 * 包括：原始类型,、String、other CharSequence, a Number, a Date, a URI, a URL, a Locale
 * or a Class.
 * 
 * @author 薛超
 * @since 2019年12月8日
 * @version 1.0.8
 */
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Sign {
	String SIGN = "Sign";// 签名参数
	String TIMESTAMP = "Timestamp";// 时间戳
}