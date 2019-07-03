package com.chao.cloud.im.ai.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.InitializingBean;

import com.chao.cloud.im.ai.config.BAiConfig;
import com.chao.cloud.im.ai.constant.AiConstant;
import com.chao.cloud.im.ai.model.BAiResp;
import com.chao.cloud.im.ai.model.BAiResp.Result.Response;
import com.chao.cloud.im.ai.model.BAiResp.Result.Response.Action;
import com.chao.cloud.im.ai.service.BAiUnitService;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;

/**
 * 
 * @功能：百度机器人
 * @author： 薛超
 * @时间： 2019年7月3日
 * @version 1.0.0
 */
public class BAiUnitServiceImpl implements BAiUnitService, InitializingBean {

	private BAiConfig config;

	public void setConfig(BAiConfig config) {
		this.config = config;
	}

	@Override
	public List<String> text(String text) {
		// 请求参数
		String params = "{\"log_id\":\"UNITTEST_10000\",\"version\":\"2.0\",\"service_id\":\"S19840\",\"session_id\":\"S19840\","
				+ "\"skill_ids\":[\"65191\",\"65203\"],"//
				+ "\"request\":{\"query\":\""//
				+ text//
				+ "\",\"user_id\":\"88888\"},"//
				+ "\"dialog_state\":{\"contexts\":{"//
				+ "\"SYS_REMEMBERED_SKILLS\":[\"65191\",\"65203\"]}}}";

		String json = HttpUtil.post(
				"https://aip.baidubce.com/rpc/2.0/unit/service/chat?access_token=" + config.getAccessToken(), params);

		BAiResp resp = JSONUtil.toBean(json, BAiResp.class);
		if (!AiConstant.TAI_SUCCESS.equals(resp.getError_code())) {
			return CollUtil.toList(resp.getError_msg());
		}
		List<Response> list = resp.getResult().getResponse_list();
		if (CollUtil.isNotEmpty(list)) {
			Response first = CollUtil.getFirst(list);
			return first.getAction_list().stream().map(Action::getSay).collect(Collectors.toList());
		}
		return CollUtil.toList(AiConstant.ERROR_RESULT);

	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(config);
	}

}
