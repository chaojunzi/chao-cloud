package com.chao.cloud.common.config.redis.annotation;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties.Lettuce;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties.Pool;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConfiguration;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.chao.cloud.common.config.redis.IRedisService;
import com.chao.cloud.common.config.redis.impl.RedisServiceImpl;
import com.chao.cloud.common.core.ApplicationOperation;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;

/**
 * 动态redis配置
 * 
 * @author 薛超
 * @since 2020年3月17日
 * @version 1.0.9
 */
@Configuration
@ConfigurationProperties(prefix = "spring.redis.dynamic")
public class RedisDynamicConfig implements InitializingBean {

	private String primary = "master";

	private boolean autowired = false;

	/**
	 * lettuce 实现
	 */
	private Map<String, RedisProperties> datasource;
	/**
	 * RedisTemplate
	 */
	private final Map<String, IRedisService> redisServiceMap = new ConcurrentHashMap<>();

	/**
	 * 获取redis数据源
	 * 
	 * @param key 数据源的别名
	 * @return {@link IRedisService}
	 */
	public IRedisService getRedisService(String key) {
		Assert.notEmpty(datasource, "redis-datasource 配置为空");
		// 校验key
		Assert.notBlank(key, "key 不能为空");
		// 获取 IRedisService
		IRedisService redisService = redisServiceMap.get(key);
		Assert.notNull(redisService, "无效的key={}", key);
		return redisService;
	}

	/**
	 * 注入spring
	 * 
	 * @param redisDynamicConfig {@link RedisDynamicConfig}
	 * @return {@link IRedisService}
	 */
	@Bean
	@Primary
	public IRedisService redisService(RedisDynamicConfig redisDynamicConfig) {
		return redisDynamicConfig.getRedisService(redisDynamicConfig.primary);
	}

	/**
	 * *************************redis初始化配置******************
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notBlank(primary, "primary 不能为空");
		Assert.notEmpty(datasource, "redis-datasource 配置为空");
		// 配置redis 信息
		datasource.forEach((k, redisProp) -> {
			// 获取连接
			GenericObjectPoolConfig poolConfig = buildPoolConfig(redisProp.getLettuce());
			LettuceClientConfiguration clientConfiguration = LettucePoolingClientConfiguration.builder()//
					.poolConfig(poolConfig)//
					.build();
			// redis 连接配置
			RedisConfiguration redisConfiguration = this.getRedisConfiguration(redisProp);
			//
			LettuceConnectionFactory connectionFactory = new LettuceConnectionFactory(redisConfiguration,
					clientConfiguration);
			connectionFactory.afterPropertiesSet();
			// 生成客户端
			StringRedisTemplate stringRedisTemplate = new StringRedisTemplate(connectionFactory);
			//
			IRedisService redisService = new RedisServiceImpl(stringRedisTemplate);
			redisServiceMap.put(k, redisService);
			// 注入spring容器
			if (autowired) {
				// get the BeanDefinitionBuilder
				BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder
						.genericBeanDefinition(RedisServiceImpl.class).addConstructorArgValue(stringRedisTemplate);
				ApplicationOperation.registerBean(StrUtil.format("{}RedisService", k),
						beanDefinitionBuilder.getBeanDefinition());
			}

		});

	}

	/**
	 * 构造对象池
	 * 
	 * @param lettuce {@link Lettuce}
	 * @return {@link GenericObjectPoolConfig}
	 */
	@SuppressWarnings("rawtypes")
	private GenericObjectPoolConfig buildPoolConfig(Lettuce lettuce) {
		Pool pool = lettuce.getPool();
		GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
		if (pool != null) {
			poolConfig.setMaxTotal(pool.getMaxActive());
			poolConfig.setMaxIdle(pool.getMaxIdle());
			poolConfig.setMaxWaitMillis(pool.getMaxWait().toMillis());
			poolConfig.setMinIdle(pool.getMinIdle());
		}
		poolConfig.setEvictorShutdownTimeoutMillis(lettuce.getShutdownTimeout().toMillis());
		return poolConfig;
	}

	/**
	 * redis配置-优先集群
	 * 
	 * @param properties {@link RedisProperties}
	 * @return {@link RedisConfiguration}
	 */
	private RedisConfiguration getRedisConfiguration(RedisProperties properties) {
		if (properties.getCluster() == null) {
			return getStandaloneConfig(properties);
		}
		RedisProperties.Cluster clusterProperties = properties.getCluster();
		RedisClusterConfiguration config = new RedisClusterConfiguration(clusterProperties.getNodes());
		if (clusterProperties.getMaxRedirects() != null) {
			config.setMaxRedirects(clusterProperties.getMaxRedirects());
		}
		if (properties.getPassword() != null) {
			config.setPassword(RedisPassword.of(properties.getPassword()));
		}
		return config;
	}

	/**
	 * 单机版配置
	 * 
	 * @param properties {@link RedisProperties}
	 * @return {@link RedisStandaloneConfiguration}
	 */
	private RedisStandaloneConfiguration getStandaloneConfig(RedisProperties properties) {
		RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
		config.setDatabase(properties.getDatabase());
		config.setHostName(properties.getHost());
		config.setPort(properties.getPort());
		config.setPassword(RedisPassword.of(properties.getPassword()));
		return config;
	}

	public String getPrimary() {
		return primary;
	}

	public void setPrimary(String primary) {
		this.primary = primary;
	}

	public boolean isAutowired() {
		return autowired;
	}

	public void setAutowired(boolean autowired) {
		this.autowired = autowired;
	}

	public Map<String, RedisProperties> getDatasource() {
		return datasource;
	}

	public void setDatasource(Map<String, RedisProperties> datasource) {
		this.datasource = datasource;
	}

}
