package com.chao.cloud.common.web.crypto;

import java.io.InputStream;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class SecretHttpMessage implements HttpInputMessage {
	private InputStream body;
	private HttpHeaders httpHeaders;

	@Override
	public InputStream getBody() {
		return this.body;
	}

	@Override
	public HttpHeaders getHeaders() {
		return this.httpHeaders;
	}
}