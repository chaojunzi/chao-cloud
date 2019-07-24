package com.chao.cloud.common.config.exception;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.google.common.net.MediaType;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class ErrorUtil {

	private final static String ERROR_403 = "public/error/403.html";
	private final static String ERROR_404 = "public/error/404.html";

	private final static List<String> ERROR_403_LIST = CollUtil.toList(//
			"org.apache.shiro.authz.UnauthorizedException"// shiro 权限管理
	);

	public static void writeErrorHtml(HttpServletResponse response, Class<? extends Exception> exceptionClass) {
		writeErrorHtml(response, ERROR_403_LIST.contains(exceptionClass.getName()) ? ERROR_403 : ERROR_404);
	}

	public static void writeErrorHtml(HttpServletResponse response) {
		writeErrorHtml(response, ERROR_404);
	}

	public static void writeErrorHtml(HttpServletResponse response, String errorFile) {
		response.setContentType(MediaType.HTML_UTF_8.toString());
		try (OutputStream out = response.getOutputStream(); InputStream in = ResourceUtil.getStream(errorFile)) {
			IoUtil.copy(in, out);
		} catch (Exception e) {
			log.error("{}", e);
		}
	}

}
