package com.chao.cloud.common.config.auth.core;

import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

/**
 * 用户接口
 * @author 薛超
 * @since 2019年8月1日
 * @version 1.0.5
 */
public interface IAuthUser {

	String USER_LOGIN_TOKEN = "USER_LOGIN_TOKEN:";
	String REQUEST_PARAM_TOKEN = "token";

	/**
	 * id
	 * @return Integer
	 */
	Integer getId();

	/**
	 * 用户状态
	 * @return Integer
	 */
	Integer getStatus();

	/**
	 * 用户类型
	 * @return Integer
	 */
	Integer getUserType();

	/**
	 * 登录凭证
	 * @return String
	 */
	String getToken();

	/**
	 * className
	 * @return String
	 */
	String getClassName();

	static IAuthUser parseAuthUser(String json) {
		if (JSONUtil.isJsonObj(json)) {
			JSONObject obj = JSONUtil.parseObj(json);
			// 开始解析
			String className = obj.getStr("className");
			if (StrUtil.isNotBlank(className)) {
				return obj.toBean(ClassUtil.loadClass(className));
			}
		}
		return null;
	}

}
