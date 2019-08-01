package com.chao.cloud.common.extra.voice;

import java.io.InputStream;
import java.util.HashMap;

/**
 * 语音识别服务
 * @author 薛超
 * @since 2019年8月1日
 * @version 1.0.5
 */
public interface SpeechRecognitionService {

	/**
	 * 语音转文字
	 * @param in 语音输入流
	 * @param options 操作map对象
	 * @return 解析结果
	 * @throws Exception 解析时抛出的异常
	 */
	String speechToText(InputStream in, HashMap<String, Object> options) throws Exception;

	/**
	 * 语音转文字-急速版
	 * @param in 语音输入流
	 * @param options 操作map对象
	 * @return 解析结果
	 * @throws Exception 解析时抛出的异常
	 */
	String topspeedSpeechToText(InputStream in, HashMap<String, Object> options) throws Exception;

	/**
	 * 参数
	 */
	String KEY_DEV_PID = "dev_pid";

	/**
	 * 普通话(支持简单的英文识别) 
	 * /搜索模型
	 * /无标点
	 * /支持自定义词库
	 */
	int DEV_PID_1536 = 1536;
	/**
	 *  普通话(纯中文识别)
	 *  /输入法模型 
	 *  /有标点
	 *  /不支持自定义词库
	 *  /默认!!!
	 */
	int DEV_PID_1537 = 1537;
	/**
	 *  英语
	 *  /无标点
	 *  /不支持自定义词库
	 */
	int DEV_PID_1737 = 1737;
	/**
	 * 粤语
	 * /有标点
	 * /不支持自定义词库
	 */
	int DEV_PID_1637 = 1637;
	/**
	 * 四川话
	 * /有标点
	 * /不支持自定义词库
	 */
	int DEV_PID_1837 = 1837;
	/**
	 * 普通话远场
	 * /有标点
	 * /不支持自定义词库
	 */
	int DEV_PID_1936 = 1936;
	/**
	 *  极速识别
	 *  普通话(纯中文识别)
	 *  /输入法模型 
	 *  /有标点
	 *  /支持自定义词库
	 */
	int DEV_PID_80001 = 80001;

}
