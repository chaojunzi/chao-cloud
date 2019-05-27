package com.chao.cloud.admin.system.domain.dto;

import java.io.Serializable;

import lombok.Data;

/**
 * 
 * @功能：部门管理
 * @author： 薛超
 * @时间： 2019年5月27日
 * @version 1.0.0
 */
@Data
public class DeptDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	//
	private Long deptId;
	// 上级部门ID，一级部门为0
	private Long parentId;
	// 部门名称
	private String name;
	// 排序
	private Integer orderNum;
	// 是否删除 -1：已删除 0：正常
	private Integer delFlag;

}
