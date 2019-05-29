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
public class SysUser implements Serializable {

	private static final long serialVersionUID = 1L;

	@TableId(value = "user_id", type = IdType.AUTO)
	private Long userId;

	/**
	 * 用户名
	 */
	private String username;

	private String name;

	/**
	 * 密码
	 */
	private String password;

	private Long deptId;

	private String deptName;

	/**
	 * 邮箱
	 */
	private String email;

	/**
	 * 手机号
	 */
	private String mobile;

	/**
	 * 0.禁用1.正常
	 */
	private Integer status;

	@DateTimeFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
	private Date createTime;

}
