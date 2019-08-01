package com.chao.cloud.common.extra.mybatis.generator.menu;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 菜单映射
 * @author 薛超
 * @since 2019年8月1日
 * @version 1.0.5
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface MenuMapping {
	/**
	 * 标题
	 * @return 菜单标题
	 */
	String value() default "";

	/**
	 * 类型
	 * @return 菜单类型枚举 {@link MenuEnum}
	 */
	MenuEnum type() default MenuEnum.BUTTON;

}
