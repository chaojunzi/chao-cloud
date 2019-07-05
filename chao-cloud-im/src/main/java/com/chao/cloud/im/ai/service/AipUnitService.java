package com.chao.cloud.im.ai.service;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONObject;
import org.springframework.beans.factory.InitializingBean;

import com.baidu.aip.client.BaseClient;
import com.baidu.aip.http.AipRequest;
import com.baidu.aip.http.EBodyFormat;
import com.chao.cloud.im.ai.constant.AiConstant;
import com.chao.cloud.im.ai.model.BAiResp;
import com.chao.cloud.im.ai.model.BAiResp.Result.Response;
import com.chao.cloud.im.ai.model.BAiResp.Result.Response.Action;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.lang.Console;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;

/**
 * http://ai.baidu.com/docs#/UNIT-v2-service-API/top
 * @功能：百度：UNIT机器人对话API文档
 * @author： 薛超
 * @时间： 2019年7月5日
 * @version 1.0.0
 */
public class AipUnitService extends BaseClient implements InitializingBean {

	public AipUnitService(String appId, String apiKey, String secretKey) {
		super(appId, apiKey, secretKey);
	}

	private String parmTemplate;// request 模板

	/**
	 * 根据文本返回
	 * @param text 问题
	 * @param userId 内部用户id
	 * @return
	 */
	public String text(String text, Integer userId) {
		AipRequest request = new AipRequest();
		preOperation(request);
		// 请求参数
		String params = StrUtil.format(parmTemplate, text, userId);
		HashMap<String, Object> map = JSONUtil.toBean(params, HashMap.class);
		request.setBody(map);
		// 设置请求路径
		request.setUri(AiConstant.BAI_URL);
		request.setBodyFormat(EBodyFormat.RAW_JSON);
		postOperation(request);
		JSONObject server = requestServer(request);
		BAiResp resp = JSONUtil.toBean(server.toString(), BAiResp.class);
		if (!AiConstant.TAI_SUCCESS.equals(resp.getError_code())) {
			return resp.getError_msg();
		}
		List<Response> list = resp.getResult().getResponse_list();
		if (CollUtil.isNotEmpty(list)) {
			// 获取需要解析的数据
			list = list.stream()
					.filter(l -> (!AiConstant.GREET_CODE.equals(l.getOrigin())
							|| AiConstant.CONFIDENCE_LIMIT < l.getSchema().getIntent_confidence()) // 问候类型 大于50
							&& CollUtil.isNotEmpty(l.getAction_list())// action不能为空
							&& l.getAction_list().stream()//
									.filter(a -> ReUtil.isMatch(AiConstant.BAI_OK_TYPE, a.getType())).count() > 0)// 只解析这几种类型
					.collect(Collectors.toList());
			// 继续解析
			Console.log(JSONUtil.toJsonStr(list));
			Response first = CollUtil.getFirst(list);
			// 判断信任值
			if (!BeanUtil.isEmpty(first) && CollUtil.isNotEmpty(first.getAction_list())) {
				List<Action> action_list = first.getAction_list();
				// 随机取一条
				int index = RandomUtil.randomInt(action_list.size());
				return action_list.get(index).getSay();
			}
		}
		return AiConstant.ERROR_RESULT;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// 解析json
		try (InputStream in = ResourceUtil.getStream(AiConstant.BAI_REQUEST_TEMPLATE);) {
			parmTemplate = IoUtil.read(in, CharsetUtil.CHARSET_UTF_8);
		}
	}

}
