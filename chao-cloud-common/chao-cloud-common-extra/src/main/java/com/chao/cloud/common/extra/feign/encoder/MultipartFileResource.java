package com.chao.cloud.common.extra.feign.encoder;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.core.io.InputStreamResource;

/**
 * MultipartFileResource
 * @author 薛超
 * @since 2019年8月1日
 * @version 1.0.5
 */
public class MultipartFileResource extends InputStreamResource {

	private String filename;
	private long size;

	public MultipartFileResource(String filename, long size, InputStream inputStream) {
		super(inputStream);
		this.size = size;
		this.filename = filename;
	}

	@Override
	public String getFilename() {
		return this.filename;
	}

	@Override
	public InputStream getInputStream() throws IOException, IllegalStateException {
		return super.getInputStream();
	}

	@Override
	public long contentLength() {
		return size;
	}

}