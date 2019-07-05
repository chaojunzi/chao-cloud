package com.chao.cloud.im.ai.service;

import org.springframework.beans.factory.InitializingBean;

import com.chao.cloud.common.exception.BusinessException;
import com.chao.cloud.im.ai.config.TAiConfig;
import com.chao.cloud.im.ai.constant.AiConstant;
import com.chao.cloud.im.ai.model.TAiResp;

import cn.hutool.core.lang.Assert;
import cn.hutool.json.JSONUtil;
import cn.xsshome.taip.imageclassify.TAipImageClassify;

/**
 * 
 * @功能：图像识别
 * @author： 薛超
 * @时间： 2019年7月5日
 * @version 1.0.0
 */
public class TAipImageClassifyService implements InitializingBean {

	private TAiConfig config;
	private TAipImageClassify client;

	public void setConfig(TAiConfig config) {
		this.config = config;
	}

	public String visionImg(byte[] image) throws Exception {
		String result = client.visionImgtotext(image, config.getSession());
		// 解析
		TAiResp tAiResp = JSONUtil.toBean(result, TAiResp.class);
		if (AiConstant.TAI_SUCCESS.equals(tAiResp.getRet())) {
			return tAiResp.getData().getText();
		}
		throw new BusinessException("图片识别失败");
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(config);
		client = new TAipImageClassify(config.getAppId(), config.getAppKey());
	}
}
