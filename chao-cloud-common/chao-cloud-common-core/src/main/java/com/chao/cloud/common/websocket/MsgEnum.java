package com.chao.cloud.common.websocket;

/**
 * 消息类型
 * 
 * @author 薛超
 * @since 2021年7月29日
 * @version 1.0.0
 */
public enum MsgEnum {

	CLOSE(0), // 关闭
	START(1), // 开始连接
	TIPS(2), // 提示
	RECEIVE(3), // 接收消息
	REFRESH(4), // 刷新
	WAIT(5)// 等待
	;

	public Integer type;

	MsgEnum(Integer type) {
		this.type = type;
	}

}
