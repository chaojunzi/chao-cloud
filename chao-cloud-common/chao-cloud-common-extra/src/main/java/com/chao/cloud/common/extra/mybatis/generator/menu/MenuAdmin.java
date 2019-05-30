package com.chao.cloud.common.extra.mybatis.generator.menu;

import lombok.Data;

@Data
public class MenuAdmin {

	private Long menuId;

	/**
	 * 父菜单ID，一级菜单为0
	 */
	private Long parentId;

	/**
	 * 菜单名称
	 */
	private String name;

	/**
	 * 菜单URL
	 */
	private String url;

	/**
	 * 授权(多个用逗号分隔，如：user:list,user:create)
	 */
	private String perms;

	/**
	 * 图标
	 */
	private String icon = "layui-icon layui-icon-rate-half";

	/**
	 * 类型   0：目录   1：菜单   2：按钮
	 */
	private Integer type;
}
