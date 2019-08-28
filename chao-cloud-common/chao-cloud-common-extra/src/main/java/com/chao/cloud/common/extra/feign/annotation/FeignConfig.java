package com.chao.cloud.common.extra.feign.annotation;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.chao.cloud.common.extra.feign.encoder.FeignSpringFormEncoder;
import com.chao.cloud.common.extra.feign.proxy.FeignFallbackProxy;

import feign.Feign;
import feign.codec.Encoder;
import lombok.Data;
import okhttp3.ConnectionPool;

/**
 * openFeign-okhttp 配置
 * @author 薛超
 * @since 2019年8月26日
 * @version 1.0.7
 */
@Configuration
@ConditionalOnClass(Feign.class)
@AutoConfigureBefore(FeignAutoConfiguration.class)
@ConfigurationProperties(prefix = "chao.cloud.feign.request")
@Data
public class FeignConfig {
	private Integer connectTimeout = 120;// 连接超时
	private Integer readTimeout = 120;// 读超时
	private Integer writeTimeout = 120;// 写超时
	private Integer maxIdleConnections = 10;// 最大连接数
	private long keepAliveDuration = 5;// 保持连接时间（分钟）

	@Autowired
	private ObjectFactory<HttpMessageConverters> messageConverters;

	@Bean
	public okhttp3.OkHttpClient okHttpClient(FeignConfig config) {
		return new okhttp3.OkHttpClient.Builder()//
				.readTimeout(config.readTimeout, TimeUnit.SECONDS)//
				.connectTimeout(config.connectTimeout, TimeUnit.SECONDS)//
				.writeTimeout(config.writeTimeout, TimeUnit.SECONDS)//
				.retryOnConnectionFailure(true)// 是否自动重连
				.connectionPool(
						new ConnectionPool(config.maxIdleConnections, config.keepAliveDuration, TimeUnit.MINUTES))//
				.build();
	}

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

	/**
	 * aop处理
	 * @return {@link FeignFallbackProxy}
	 */
	@Bean
	public FeignFallbackProxy FeignFallbackProxy() {
		return new FeignFallbackProxy();
	}

}