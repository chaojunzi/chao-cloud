package com.chao.cloud.common.extra.mybatis.generator.menu;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 菜单映射
 * @功能：
 * @author： 薛超
 * @时间： 2019年5月29日
 * @version 1.0.0
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface MenuMapping {
	/**
	 * 描述
	 * @return
	 */
	String value() default "";

	/**
	 * 类型
	 * @return
	 */
	MenuEnum type() default MenuEnum.BUTTON;

}
