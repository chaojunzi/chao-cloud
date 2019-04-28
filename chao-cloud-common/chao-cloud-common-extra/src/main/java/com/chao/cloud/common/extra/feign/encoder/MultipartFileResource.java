package com.chao.cloud.common.extra.feign.encoder;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.core.io.InputStreamResource;

/**
 * @author dax.
 * @version v1.0
 * @since 2017/12/27 11:15
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