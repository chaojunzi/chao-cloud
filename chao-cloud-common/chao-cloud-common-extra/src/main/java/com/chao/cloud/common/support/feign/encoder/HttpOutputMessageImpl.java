package com.chao.cloud.common.support.feign.encoder;

import java.io.OutputStream;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpOutputMessage;

/**
 * The type Http output message.
 *
 * @author dax.
 * @version v1.0
 * @since 2017 /12/27 11:21
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