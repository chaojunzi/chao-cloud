package com.chao.cloud.admin.sys.dal.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @功能：
 * @author： 超君子
 * @时间：2019-05-28
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SysDept implements Serializable {

	private static final long serialVersionUID = 1L;

	@TableId(value = "dept_id", type = IdType.AUTO)
	private Integer deptId;

	/**
	 * 上级部门ID，一级部门为0
	 */
	private Integer parentId;

	/**
	 * 部门名称
	 */
	private String name;

	/**
	 * 排序
	 */
	private Integer orderNum;

	/**
	 * 是否删除  0：已删除  1：正常
	 */
	private Integer delFlag;

}
