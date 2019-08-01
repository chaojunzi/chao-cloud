package com.chao.cloud.common.base;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import cn.hutool.core.util.StrUtil;

public interface BaseHttpServlet extends BaseLogger {

	/**
	 * 获取request
	 * 
	 * @return {@link HttpServletRequest}
	 */
	default HttpServletRequest getRequest() {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
				.getRequest();
		return request;
	}

	/**
	 * 获取response
	 * 
	 * @return {@link HttpServletResponse}
	 */
	default HttpServletResponse getResponse() {
		HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
				.getResponse();
		return response;
	}

	/**
	 * 获取请求地址
	 * 
	 * @param request httpRequest
	 * @param realm_current 例如：http://www.dcdyxxx.com 不要+/ 当前域名
	 * @return String
	 */
	default String getRequestEncodeUrl(HttpServletRequest request, String realm_current) {
		try {
			String param = "";
			String url = realm_current + request.getRequestURI();
			if (request.getQueryString() != null) {
				param = "?" + request.getQueryString();
			}
			return "?redirectUrl=" + java.net.URLEncoder.encode(url + param, "UTF-8");
		} catch (Exception e) {
		}
		return "";
	}

	/**
	 * 判断方法是不是ajax
	 * @param request httpRequest
	 * @return boolean
	 */
	default boolean requestIsAjax(HttpServletRequest request) {
		boolean isAjax = false;
		if (!StrUtil.isBlank(request.getHeader("x-requested-with"))
				&& request.getHeader("x-requested-with").equals("XMLHttpRequest")) {
			isAjax = true;
		}
		return isAjax;
	}

	/**
	 * 是否包含
	 * @param object 对象
	 * @return boolean
	 */
	default boolean isInclude(Object object) {
		return (object instanceof HttpServletRequest//
				|| object instanceof HttpServletResponse//
				|| object instanceof HttpSession//
		);
	}

}
