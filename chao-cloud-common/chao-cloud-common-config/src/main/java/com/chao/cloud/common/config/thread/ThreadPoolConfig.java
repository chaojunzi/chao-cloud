package com.chao.cloud.common.config.thread;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class ThreadPoolConfig {

	private static final String THREAD_POOL_ASYNC = "thread.pool.async";

	/**
	 * 自定义异步线程池
	 * @return  {@link ThreadPoolTaskExecutor}
	 */
	@Bean
	@ConfigurationProperties(prefix = THREAD_POOL_ASYNC)
	public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(10);// 默认10个线程
		executor.setKeepAliveSeconds(300);// 默认保持300秒
		executor.setMaxPoolSize(30);// 最大线程池30个
		executor.setQueueCapacity(75);// 队列75个
		executor.setThreadNamePrefix("Async-Executor");
		executor.setRejectedExecutionHandler(new java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy());
		return executor;
	}

}
