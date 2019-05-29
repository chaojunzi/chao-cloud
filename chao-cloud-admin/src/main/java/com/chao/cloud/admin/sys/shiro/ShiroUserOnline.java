package com.chao.cloud.admin.sys.shiro;

import java.util.Date;

import lombok.Data;

@Data
public class ShiroUserOnline {

	private String id;

	private String userId;

	private String username;

	/**
	 * 用户主机地址
	 */
	private String host;

	/**
	 * 用户登录时系统IP
	 */
	private String systemHost;

	/**
	 * 用户浏览器类型
	 */
	private String userAgent;

	/**
	 * 在线状态
	 */
	private String status = "on_line";

	/**
	 * session创建时间
	 */
	private Date startTimestamp;
	/**
	 * session最后访问时间
	 */
	private Date lastAccessTime;

	/**
	 * 超时时间
	 */
	private Long timeout;

	/**
	 * 备份的当前用户会话
	 */
	private String onlineSession;

}
