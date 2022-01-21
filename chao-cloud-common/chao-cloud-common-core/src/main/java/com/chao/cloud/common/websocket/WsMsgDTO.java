package com.chao.cloud.common.websocket;

import cn.hutool.core.util.URLUtil;
import lombok.Data;

/**
 * 消息返回值
 * 
 * @author 薛超
 * @since 2021年7月29日
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
	 * 
	 * @param msg
	 */
	public static WsMsgDTO buildMsg(MsgEnum type, String msg) {
		WsMsgDTO resp = new WsMsgDTO();
		resp.setType(type.type);
		// url编码-否则出现前端无法识别的问题
		resp.setMsg(URLUtil.encodeAll(msg));
		return resp;
	}

	public static WsMsgDTO buildMsg(MsgEnum type, Object msg) {
		WsMsgDTO resp = new WsMsgDTO();
		resp.setType(type.type);
		resp.setMsg(msg);
		return resp;
	}

}
