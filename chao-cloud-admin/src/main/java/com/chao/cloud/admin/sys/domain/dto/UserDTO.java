package com.chao.cloud.admin.sys.domain.dto;

import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import cn.hutool.core.date.DatePattern;
import lombok.Data;

/**
 * 
 * @功能：
 * @author： 薛超
 * @时间： 2019年5月27日
 * @version 1.0.0
 */
@Data
public class UserDTO {
	//
	private Long userId;
	// 用户名
	private String username;
	//
	private String name;
	// 密码
	private String password;
	//
	private Long deptId;
	//
	private String deptName;
	// 邮箱
	private String email;
	// 手机号
	private String mobile;
	// 0.禁用1.正常
	private Byte status;
	// 角色
	private List<Long> roleIds;
	//
	@DateTimeFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
	private Date createTime;

}
