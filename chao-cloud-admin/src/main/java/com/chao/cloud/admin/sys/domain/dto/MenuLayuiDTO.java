package com.chao.cloud.admin.sys.domain.dto;

import java.util.List;

import lombok.Data;

@Data
public class MenuLayuiDTO {

	private Integer menuId;// 节点id
	private String title;// 名称
	private String icon;// 图标
	private String href;// 链接
	private String target;// 跳转方式
	private Boolean spread = false;// 是否展开
	private List<MenuLayuiDTO> children;
}
