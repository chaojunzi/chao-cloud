package com.chao.cloud.im.ai.constant;

import java.util.List;

import cn.hutool.core.collection.CollUtil;

/**
 * 
 * @功能：
 * @author： 薛超
 * @时间： 2019年7月3日
 * @version 1.0.0
 */
public interface AiConstant {

	/**
	 * 机器人白名单
	 */
	List<Integer> REBOT_ID_LIST = CollUtil.toList(AiConstant.XUE, AiConstant.CHAO);

	int XUE = 3;

	int CHAO = 4;

	int CONFIDENCE_LIMIT = 50;
	/**
	 * 问候编码
	 */
	String GREET_CODE = "65192";

	/**
	 * 腾讯响应成功返回码
	 */
	Integer TAI_SUCCESS = 0;
	/**
	 * 错误返回值
	 */
	String ERROR_RESULT = "抱歉，无法听懂您的描述，请不要输入特殊字符，包括标点符号";
	/**
	 * 百度api路径
	 */
	String BAI_URL = "https://aip.baidubce.com/rpc/2.0/unit/service/chat";
	/**
	 * 百度请求路径
	 */
	String BAI_REQUEST_TEMPLATE = "static/json/bai_resquest.json";
	/**
	 * 错误的响应状态
	 */
	String BAI_ERROR_TYPE = "failure";
	/**
	 * 要解析的状态
	 */
	String BAI_OK_TYPE = "(clarify|satisfy|chat)";
}
