package com.chao.cloud.common.config.exception;

import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import com.google.common.net.MediaType;

import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class ErrorUtil {

	public static void main(String[] args) {
		String base64 = ImgUtil.toBase64(ImgUtil.read("D:/404.png"), "png");
		System.out.println(base64);
	}

	public static void writeErrorHtml(HttpServletResponse response) {
		response.setContentType(MediaType.HTML_UTF_8.toString());
		try (OutputStream out = response.getOutputStream();
				InputStream in = ResourceUtil.getStream("public/error/404.html");) {
			IoUtil.copy(in, out);
		} catch (Exception e) {
			log.error("{}", e);
		}
	}

}
