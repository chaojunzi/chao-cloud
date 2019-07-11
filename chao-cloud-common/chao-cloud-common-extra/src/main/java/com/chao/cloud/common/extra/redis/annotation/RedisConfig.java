package com.chao.cloud.common.extra.redis.annotation;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.chao.cloud.common.extra.redis.IRedisService;
import com.chao.cloud.common.extra.redis.impl.RedisServiceImpl;

/**
 * redis缓存配置
 * @功能：https://github.com/whvcse/RedisUtil
 * @author： 薛超
 * @时间：2019年7月11日
 * @version 1.0.1
 */
@Configuration
public class RedisConfig {

	@Bean
	public IRedisService redisService(StringRedisTemplate redisTemplate) {
		return new RedisServiceImpl(redisTemplate);
	}

}