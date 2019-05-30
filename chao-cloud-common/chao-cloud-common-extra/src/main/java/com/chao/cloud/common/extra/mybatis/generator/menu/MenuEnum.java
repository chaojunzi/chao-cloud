package com.chao.cloud.common.extra.mybatis.generator.menu;

/**
 * 
 * @功能：菜单枚举
 * @author： 薛超
 * @时间： 2019年5月29日
 * @version 1.0.0
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
