package com.chao.cloud.admin.sys.domain.vo;

import lombok.Data;

/**
 * 
 * @功能：用户vo
 * @author： 薛超
 * @时间： 2019年5月27日
 * @version 1.0.0
 */
@Data
public class UserVO {

	/**
	 * 旧密码
	 */
	private String pwdOld;
	/**
	 * 新密码
	 */
	private String pwdNew;

}
