package com.chao.cloud.generator.websocket.model;

import lombok.Data;

/**
 * 返回值
 * @功能：
 * @author： 薛超
 * @时间：2019年6月19日
 * @version 1.0.0
 */
@Data
public class WsMsgDTO {

	/**
	 * 数据类型
	 */
	private Integer type;

	/**
	 * 数据内容
	 */
	private Object msg;

	/**
	 * 提示消息
	 * @param msg
	 * @return
	 */
	public static WsMsgDTO buildMsg(MsgEnum type, Object msg) {
		WsMsgDTO resp = new WsMsgDTO();
		resp.setType(type.type);
		resp.setMsg(msg);
		return resp;
	}

}
