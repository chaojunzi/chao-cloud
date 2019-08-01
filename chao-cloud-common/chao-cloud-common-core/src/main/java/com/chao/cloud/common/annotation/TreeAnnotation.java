package com.chao.cloud.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.chao.cloud.common.constant.TreeEnum;

/**
 * 树形数据解析
 * @author 薛超
 * @since 2019年8月1日
 * @version 1.0.0
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface TreeAnnotation {
	/**
	 * 类型
	 * @return {@link TreeEnum}
	 */
	TreeEnum value();

}
