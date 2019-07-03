package com.chao.cloud.im.websocket.constant;

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
	ON_LINE(3), // 上线
	OFF_LINE(4), // 下线
	CHAT(5), // 聊天
	OFF_MSG(6), // 离线消息
	OTHER_INPUT(7), // 对方正在输入
	;

	public Integer type;

	MsgEnum(Integer type) {
		this.type = type;
	}

}
