package com.chao.cloud.im.ai.service;

import org.springframework.beans.factory.InitializingBean;

import com.chao.cloud.im.ai.config.TAiConfig;
import com.chao.cloud.im.ai.constant.AiConstant;
import com.chao.cloud.im.ai.model.TAiResp;

import cn.hutool.core.lang.Assert;
import cn.hutool.json.JSONUtil;
import cn.xsshome.taip.nlp.TAipNlp;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @功能：腾讯机器人
 * @author： 薛超
 * @时间： 2019年7月3日
 * @version 1.0.0
 */
@Slf4j
public class TAiRobotService implements InitializingBean {

	private TAiConfig config;
	private TAipNlp client;

	public void setConfig(TAiConfig config) {
		this.config = config;
	}

	public String text(String text) {
		try {
			String result = client.nlpTextchat(config.getSession(), text);// 基础闲聊
			// 解析
			TAiResp tAiResp = JSONUtil.toBean(result, TAiResp.class);
			if (AiConstant.TAI_SUCCESS.equals(tAiResp.getRet())) {
				return tAiResp.getData().getAnswer();
			}
			log.info("[腾讯闲聊解析失败={}]", result);
		} catch (Exception e) {
			log.error("{}", e);
		}
		return AiConstant.ERROR_RESULT;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(config);
		client = new TAipNlp(config.getAppId(), config.getAppKey());
	}

}
