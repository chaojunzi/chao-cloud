package com.chao.cloud.common.extra.redis.annotation;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.nosql.redis.RedisDS;
import lombok.Data;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

/**
 * redis缓存配置
 * @功能：
 * @author： 薛超
 * @时间：2019年4月26日
 * @version 2.0
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "chao.cloud.redis.config")
public class RedisConfig extends RedisDS implements InitializingBean {

    private String host;
    private Integer port;
    private String password;
    private Integer connectionTimeout;
    private Integer soTimeout;
    private Integer database;
    private String clientName;
    private Boolean ssl;

    @Bean
    public Jedis redisService(RedisConfig redisConfig) {
        return redisConfig.getJedis();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notBlank(host, "redis host 不能为空");
        Assert.notNull(port, "redis port 不能为空");
        JedisPoolConfig config = new JedisPoolConfig();
        // 共用配置
        JedisPool pool = new JedisPool(config,
                // 地址
                host,
                // 端口
                port,
                // 连接超时
                ObjectUtil.isNull(connectionTimeout) ? Protocol.DEFAULT_TIMEOUT : connectionTimeout,
                // 读取数据超时
                ObjectUtil.isNull(soTimeout) ? Protocol.DEFAULT_TIMEOUT : soTimeout,
                // 密码
                password,
                // 数据库序号
                ObjectUtil.isNull(database) ? Protocol.DEFAULT_DATABASE : database,
                // 客户端名
                StrUtil.isBlank(clientName) ? "Chao" : clientName,
                // 是否使用SSL
                ObjectUtil.isNull(ssl) ? false : ssl,
                // SSL相关，使用默认
                null, null, null);
        ReflectUtil.setFieldValue(this, "pool", pool);
    }

    /**
     * 重写init() - 防止父类 加载报错
     */
    @Override
    public RedisDS init(String group) {
        return this;
    }
}