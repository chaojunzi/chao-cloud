package com.chao.cloud.generator.websocket.model;

/**
 * 
 * @功能：消息类型
 * @author： 薛超
 * @时间： 2019年6月27日
 * @version 1.0.0
 */
public enum MsgEnum {

	CLOSE(0), // 关闭
	START(1), // 开始连接
	TIPS(2), // 提示
	HEALTH(3), // 健康检查
	;

	public Integer type;

	MsgEnum(Integer type) {
		this.type = type;
	}

}
