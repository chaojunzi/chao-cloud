package com.chao.cloud.admin.sys.domain.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.validation.constraints.NotBlank;

import org.apache.shiro.session.Session;
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
public class UserDTO implements Serializable {

	private static final long serialVersionUID = 1L;
	//
	private Integer userId;
	// 用户名
	@NotBlank(message = "用户名不能为空")
	private String username;
	//
	private String name;
	// 密码
	private String password;
	//
	private String roles;
	//
	private Integer deptId;
	//
	private String deptName;
	// 邮箱
	private String email;
	// 手机号
	private String mobile;
	// 0.禁用1.正常
	private Byte status;
	// 角色
	private List<Integer> roleIds;
	private Set<String> perms;//权限值
	//
	@DateTimeFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
	private Date createTime;
	// 登录凭证
	private Session session;
}
