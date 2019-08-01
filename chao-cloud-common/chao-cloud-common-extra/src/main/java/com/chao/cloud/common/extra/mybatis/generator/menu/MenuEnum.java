package com.chao.cloud.common.extra.mybatis.generator.menu;

/**
 * 菜单枚举
 * @author 薛超
 * @since 2019年8月1日
 * @version 1.0.5
 */
public enum MenuEnum {
	/**
	 * 目录
	 */
	DIR(0),
	/**
	 * 菜单
	 */
	MENU(1),
	/**
	 * 按钮
	 */
	BUTTON(2);

	public Integer type;

	MenuEnum(Integer type) {
		this.type = type;
	}
}
