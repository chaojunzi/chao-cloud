package com.chao.cloud.im.controller;

import javax.servlet.http.HttpSession;

import com.chao.cloud.common.exception.BusinessException;
import com.chao.cloud.im.domain.dto.LoginDTO;

/**
 * 
 * @功能：
 * @author： 薛超
 * @时间： 2019年6月26日
 * @version 1.0.0
 */
public abstract class BaseController {

	protected static final String randomCookie = "im-login-cookie:";
	protected static final String token_key = "user";

	/**
	 * 获取当前用户
	 * @return
	 */
	protected LoginDTO getUser(HttpSession session) {
		Object obj = session.getAttribute(token_key);
		if (obj instanceof LoginDTO) {
			return (LoginDTO) obj;
		}
		throw new BusinessException("未登录");
	}

	/**
	 * 是否登录
	 * @return
	 */
	protected boolean isLogin(HttpSession session) {
		return session.getAttribute(token_key) instanceof LoginDTO;
	}

}
