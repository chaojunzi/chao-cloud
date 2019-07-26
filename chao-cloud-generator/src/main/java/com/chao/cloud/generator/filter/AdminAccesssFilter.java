package com.chao.cloud.generator.filter;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Component;

import com.chao.cloud.generator.controller.BaseController;
import com.chao.cloud.generator.domain.dto.LoginDTO;

import cn.hutool.core.util.ReUtil;

@WebFilter(urlPatterns = "/*")
@Component
public class AdminAccesssFilter extends BaseController implements Filter {

	private final String excludePath = "(login|logout|getVerify|layui|favicon.ico)";

	@Override
	public void doFilter(ServletRequest resq, ServletResponse resp, FilterChain filterChain)
			throws IOException, ServletException {
		HttpServletRequest httpResq = (HttpServletRequest) resq;
		HttpServletResponse httpResp = (HttpServletResponse) resp;
		String uri = httpResq.getRequestURI();
		if (ReUtil.contains(excludePath, uri)) {
			filterChain.doFilter(resq, resp);
		} else {
			HttpSession session = httpResq.getSession();
			LoginDTO loginSession = getSafeUser(session);
			if (loginSession != null) {
				filterChain.doFilter(resq, resp);
			} else {
				httpResp.setCharacterEncoding("UTF-8");
				httpResp.setContentType("text/html; charset=utf-8");
				PrintWriter out = null;
				out = httpResp.getWriter();
				out.print("<script>window.location.href='/logout'</script>");
				out.flush();
				if (out != null) {
					out.close();
				}

			}

		}
	}

}
