package com.chao.cloud.common.config.auth.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.chao.cloud.common.config.auth.core.AuthUserPerm;
import com.chao.cloud.common.config.auth.core.AuthUserProxy;
import com.chao.cloud.common.config.auth.core.AuthUserResolver;
import com.chao.cloud.common.extra.redis.IRedisService;
import com.chao.cloud.common.extra.redis.impl.RedisServiceImpl;

@Configuration
public class AuthUserConfig {

	@Bean
	@ConfigurationProperties(prefix = "chao.cloud.auth")
	public AuthUserPerm authUserPerm() {
		return new AuthUserPerm();
	}

	@Bean
	@ConditionalOnMissingBean(IRedisService.class)
	public IRedisService redisService(StringRedisTemplate redisTemplate) {
		return new RedisServiceImpl(redisTemplate);
	}

	@Bean
	public AuthUserResolver authUserResolver(IRedisService redisService, AuthUserPerm authUserPerm) {
		AuthUserResolver resolver = new AuthUserResolver();
		resolver.setRedisService(redisService);
		resolver.setType(authUserPerm.getResolverType());
		return resolver;
	}

	@Bean
	public AuthUserProxy userPermProxy(AuthUserPerm authUserPerm) {
		AuthUserProxy proxy = new AuthUserProxy();
		proxy.setAuthUserPerm(authUserPerm);
		return proxy;
	}

}
