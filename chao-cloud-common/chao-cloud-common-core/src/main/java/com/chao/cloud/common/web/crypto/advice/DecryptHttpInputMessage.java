package com.chao.cloud.common.web.crypto.advice;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;

import com.chao.cloud.common.web.crypto.Crypto;

import cn.hutool.core.io.IoUtil;

/**
 * 解密
 * 
 * @author 薛超
 * @since 2019年12月4日
 * @version 1.0.8
 */
public class DecryptHttpInputMessage implements HttpInputMessage {
	private HttpInputMessage inputMessage;
	private String charset;
	private Crypto crypto;

	public DecryptHttpInputMessage(HttpInputMessage inputMessage, String charset, Crypto crypto) {
		this.inputMessage = inputMessage;
		this.charset = charset;
		this.crypto = crypto;
	}

	@Override
	public InputStream getBody() throws IOException {
		String content = IoUtil.read(inputMessage.getBody(), charset);
		// 请自行使用解密方法

		//
		return new ByteArrayInputStream(content.getBytes(charset));
	}

	@Override
	public HttpHeaders getHeaders() {
		return inputMessage.getHeaders();
	}
}