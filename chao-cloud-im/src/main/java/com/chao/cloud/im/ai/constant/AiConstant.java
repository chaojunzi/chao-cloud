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

	/**
	 * 腾讯响应成功返回码
	 */
	Integer TAI_SUCCESS = 0;
	/**
	 * 错误返回值
	 */
	String ERROR_RESULT = "抱歉，无法听懂您的描述，请不要输入特殊字符，包括标点符号";

}
