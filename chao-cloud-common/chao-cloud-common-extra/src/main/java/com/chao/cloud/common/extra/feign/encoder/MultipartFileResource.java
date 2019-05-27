package com.chao.cloud.common.extra.feign.encoder;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.core.io.InputStreamResource;

/**
 * The type Http output message.
 * @功能：
 * @author： 薛超
 * @时间： 2019年5月27日
 * @version 1.0.0
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