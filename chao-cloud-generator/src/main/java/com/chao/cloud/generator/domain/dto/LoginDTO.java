package com.chao.cloud.generator.domain.dto;

import lombok.Data;

@Data
public class LoginDTO {

	private Integer id;

	/**
	 * 用户名
	 */
	private String userName;

	/**
	 * 密码
	 */
	private transient String password;

	/**
	 * 昵称
	 */
	private String nickName;

	/**
	 * 头像
	 */
	private String headImg;

	/**
	 * 状态 0.冻结 1.正常
	 */
	private Integer status;
}
