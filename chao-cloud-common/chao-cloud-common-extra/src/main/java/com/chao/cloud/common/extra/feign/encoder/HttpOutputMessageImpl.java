package com.chao.cloud.common.extra.feign.encoder;

import java.io.OutputStream;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpOutputMessage;

/**
 * The type Http output message.
 * @author 薛超
 * @since 2019年8月1日
 * @version 1.0.5
 */
public class HttpOutputMessageImpl implements HttpOutputMessage {

	private OutputStream body;
	private HttpHeaders headers;

	public HttpOutputMessageImpl(OutputStream body, HttpHeaders headers) {
		this.body = body;
		this.headers = headers;
	}

	@Override
	public OutputStream getBody() {
		return body;
	}

	@Override
	public HttpHeaders getHeaders() {
		return headers;
	}

}