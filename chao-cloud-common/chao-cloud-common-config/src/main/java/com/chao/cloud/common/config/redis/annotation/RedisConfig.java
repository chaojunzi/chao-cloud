package com.chao.cloud.common.config.redis.annotation;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.chao.cloud.common.config.redis.IRedisService;
import com.chao.cloud.common.config.redis.impl.RedisServiceImpl;

/**
 * redis缓存配置 
 * @author 薛超
 * @see <a href="https://github.com/whvcse/RedisUtil">whvcse</a>
 * @since 2019年8月1日
 * @version 1.0.5
 */
@Configuration
public class RedisConfig {

	@Bean
	public IRedisService redisService(StringRedisTemplate redisTemplate) {
		return new RedisServiceImpl(redisTemplate);
	}

}