package com.chao.cloud.im.websocket.model;

import java.util.Date;

import lombok.Data;

@Data
public class ImMsgDTO {

	/**
	 * group=群id    friend=发送者id
	 */
	private Integer id;

	/**
	 * 用户名
	 */
	private String username;

	/**
	 * 头像
	 */
	private String avatar;

	/**
	 * 消息内容
	 */
	private String content;

	/**
	 * 类型   group friend
	 */
	private String type;
	/**
	 * 时间戳
	 */
	private transient Date createTime;

	public Long getTimestamp() {
		return createTime.getTime();
	}

}
