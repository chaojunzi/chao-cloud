package com.chao.cloud.common.extra.xss.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

/**
 * 跨站点脚本编制
 * 
 * @author 薛超
 * @since 2019年11月7日
 * @version 1.0.8
 */
@WebFilter(urlPatterns = "/*")
public class XSSFilter implements Filter {

	public void init(FilterConfig filterConfig) throws ServletException {

	}

	public void doFilter(ServletRequest req, ServletResponse res, FilterChain filterChain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		request = new XSSServletRequest(request);
		filterChain.doFilter(request, res);

	}

	public void destroy() {

	}

}
