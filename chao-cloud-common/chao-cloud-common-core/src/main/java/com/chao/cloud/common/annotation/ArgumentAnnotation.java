package com.chao.cloud.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 参数转换的标志
 * @功能：
 * @author： 薛超
 * @时间： 2019年5月27日
 * @version 1.0.0
 */
@Target(ElementType.TYPE)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ArgumentAnnotation {

}
