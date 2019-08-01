package com.chao.cloud.common.extra.feign.annotation;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@ConfigurationProperties(prefix = "chao.cloud.feign.http")
@Data
public class HttpPoolConfig {

	private Integer socketTimeout = 60 * 1000;
	private Integer connectTimeout = 60 * 1000;
	private Long timeToLive = 60L;
	private Integer maxTotal = 5000;
	private Integer maxPerRoute = 100;
	private Long idleTimeout = 5L;
	private Long delay = 10 * 1000L;
	private Long period = 5 * 1000L;

	@Bean
	public HttpClient httpClient(HttpPoolConfig httpPoolConfig) {
		log.info("init feign httpclient configuration");
		// 生成默认请求配置
		RequestConfig.Builder requestConfigBuilder = RequestConfig.custom();
		// 超时时间
		requestConfigBuilder.setSocketTimeout(httpPoolConfig.socketTimeout);
		// 连接时间
		requestConfigBuilder.setConnectTimeout(httpPoolConfig.connectTimeout);
		RequestConfig defaultRequestConfig = requestConfigBuilder.build();
		// 连接池配置
		// 长连接保持30秒
		final PoolingHttpClientConnectionManager pollingConnectionManager = new PoolingHttpClientConnectionManager(
				httpPoolConfig.timeToLive, TimeUnit.MILLISECONDS);
		// 总连接数
		pollingConnectionManager.setMaxTotal(httpPoolConfig.maxTotal);
		// 同路由的并发数
		pollingConnectionManager.setDefaultMaxPerRoute(httpPoolConfig.maxPerRoute);
		// httpclient 配置
		HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
		// 保持长连接配置，需要在头添加Keep-Alive
		httpClientBuilder.setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy());
		httpClientBuilder.setConnectionManager(pollingConnectionManager);
		httpClientBuilder.setDefaultRequestConfig(defaultRequestConfig);
		HttpClient client = httpClientBuilder.build();

		// 启动定时器，定时回收过期的连接
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				// System.out.println("=====closeIdleConnections===");
				pollingConnectionManager.closeExpiredConnections();
				pollingConnectionManager.closeIdleConnections(httpPoolConfig.idleTimeout, TimeUnit.SECONDS);
			}
		}, httpPoolConfig.delay, httpPoolConfig.period);
		log.info("===== Apache httpclient 初始化连接池===");
		return client;
	}

}