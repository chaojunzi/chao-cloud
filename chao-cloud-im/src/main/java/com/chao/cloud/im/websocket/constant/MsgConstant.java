package com.chao.cloud.im.websocket.constant;

/**
 * 消息常量
 * @功能：
 * @author： 薛超
 * @时间：2019年6月19日
 * @version 1.0.0
 */
public interface MsgConstant {

	/**
	 * 未读
	 */
	Byte NO_READ = 0;
	/**
	 * 已读
	 */
	Byte IS_READ = 1;
	/**
	 * 图片正则
	 */
	String LAYIM_IMG_REGEX = "^img\\[(.*)\\]$";
	/**
	 * 文件正则
	 */
	String LAYIM_FILE_REGEX = "^file\\((.*)\\)\\[.*\\]$";

}
