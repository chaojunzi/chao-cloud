package com.chao.cloud.admin.sys.shiro;

import java.io.Serializable;

import lombok.Data;

/**
 * 
 * @功能：
 * @author： 薛超
 * @时间： 2019年5月27日
 * @version 1.0.0
 */
@Data
public class ShiroUserToken implements Serializable {
	private static final long serialVersionUID = 1L;
	private Long userId;
	private String username;
	private String name;
	private String password;
	private Long deptId;

}
