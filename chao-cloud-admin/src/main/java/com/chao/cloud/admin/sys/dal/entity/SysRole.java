package com.chao.cloud.admin.sys.dal.entity;

import java.io.Serializable;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import cn.hutool.core.date.DatePattern;
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
public class SysRole implements Serializable {

	private static final long serialVersionUID = 1L;

	@TableId(value = "role_id", type = IdType.AUTO)
	private Long roleId;

	/**
	 * 角色名称
	 */
	private String roleName;

	/**
	 * 角色标识
	 */
	private String roleSign;

	/**
	 * 备注
	 */
	private String remark;

	/**
	 * 创建用户id
	 */
	private Long userIdCreate;

	/**
	 * 创建时间
	 */
	@DateTimeFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
	private Date gmtCreate;

	/**
	 * 创建时间
	 */
	@DateTimeFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
	private Date gmtModified;

}
