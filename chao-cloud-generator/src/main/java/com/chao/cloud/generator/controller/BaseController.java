package com.chao.cloud.generator.controller;

import javax.servlet.http.HttpSession;

import com.chao.cloud.common.exception.BusinessException;
import com.chao.cloud.generator.domain.dto.LoginDTO;

public abstract class BaseController {

	protected static final String randomCookie = "login-cookie:";
	protected static final String token_key = "user";

	/**
	 * 获取当前用户
	 * @return
	 */
	protected LoginDTO getUser(HttpSession session) {
		LoginDTO user = getSafeUser(session);
		if (user != null) {
			return user;
		}
		throw new BusinessException("未登录");
	}

	/**
	 * 不会抛异常
	 * @param session
	 * @return
	 */
	protected LoginDTO getSafeUser(HttpSession session) {
		Object obj = session.getAttribute(token_key);
		return obj instanceof LoginDTO ? (LoginDTO) obj : null;
	}

	/**
	 * 是否登录
	 * @return
	 */
	protected boolean isLogin(HttpSession session) {
		return session.getAttribute(token_key) instanceof LoginDTO;
	}
}
