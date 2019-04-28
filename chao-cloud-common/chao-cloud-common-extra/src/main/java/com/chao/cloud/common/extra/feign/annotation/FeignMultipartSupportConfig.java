package com.chao.cloud.common.extra.feign.annotation;

import feign.codec.Encoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.chao.cloud.common.extra.feign.encoder.FeignSpringFormEncoder;

@Configuration
public class FeignMultipartSupportConfig {

    @Autowired
    private ObjectFactory<HttpMessageConverters> messageConverters;

    /**
     * Multipart form encoder encoder.
     *
     * @return the encoder
     */
    @Bean
    public Encoder multipartFormEncoder() {
        return new FeignSpringFormEncoder(new SpringEncoder(messageConverters));
    }

    /**
     * Multipart logger level feign . logger . level.
     *
     * @return the feign . logger . level
     */
    @Bean
    public feign.Logger.Level multipartLoggerLevel() {
        return feign.Logger.Level.FULL;
    }

}