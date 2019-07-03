package com.chao.cloud.im.websocket.constant;

/**
 * 常量
 * @功能：
 * @author： 薛超
 * @时间： 2019年6月26日
 * @version 1.0.0
 */
public interface ImConstant {

	String OTHER_INPUT = "<span style=\"color:#FF5722;\">对方正在输入...</span>";

	/**
	 * 状态
	 */
	interface Status {
		/**
		 * 冻结
		 */
		Integer FREEZE = 0;
		/**
		 * 正常
		 */
		Integer OK = 1;
	}

	/**
	 * 在线状态
	 */
	interface LineStatus {
		/**
		 * 在线
		 */
		String ON = "online";
		/**
		 * 离线
		 */
		String OFF = "offline";
	}

	/**
	 * 在线状态
	 */
	interface Type {
		/**
		 * 群消息
		 */
		String GROUP = "group";
		/**
		 * 朋友消息
		 */
		String FRIEND = "friend";
	}

}
