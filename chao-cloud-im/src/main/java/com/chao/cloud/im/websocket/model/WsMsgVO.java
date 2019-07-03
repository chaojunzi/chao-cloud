package com.chao.cloud.im.websocket.model;

import java.util.Date;

import cn.hutool.core.date.DateUtil;
import lombok.Data;

/**
 * 返回值
 * @功能：
 * @author： 薛超
 * @时间：2019年6月19日
 * @version 1.0.0
 */
@Data
public class WsMsgVO {

	/**
	 * 发送人
	 */
	private Integer fromid;

	/**
	 * 接收人
	 */
	private Integer toid;

	/**
	 * 接收人名称
	 */
	private String toname;
	/**
	 * 接收人头像
	 */
	private String toavatar;

	/**
	 * 以下为页面消息展示使用
	 */
	private Boolean system;
	/**
	 * 接收者id
	 */
	private Integer id;
	/**
	 * 发送者名称
	 */
	private String username;
	/**
	 * 内容
	 */
	private String content;
	/**
	 * 头像
	 */
	private String avatar;
	/**
	 * 消息类型  group/friend
	 */
	private String type;
	/**
	 * 毫秒数
	 */
	private Date createTime = DateUtil.date();

	public Long getTimestamp() {
		return createTime.getTime();
	}

}
