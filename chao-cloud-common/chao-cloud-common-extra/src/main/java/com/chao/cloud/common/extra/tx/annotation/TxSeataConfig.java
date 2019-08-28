package com.chao.cloud.common.extra.tx.annotation;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import com.alibaba.druid.pool.DruidDataSource;
import com.chao.cloud.common.extra.tx.proxy.TxSeataFeignProxy;

import cn.hutool.log.StaticLog;
import feign.Feign;
import io.seata.rm.datasource.DataSourceProxy;
import lombok.Data;

/**
 * 分布式事务
 * @author 薛超
 * @since 2019年8月27日
 * @version 1.0.7
 */
@Data
@ConfigurationProperties(EnableTxSeata.TX_SEATA_PREFIX)
public class TxSeataConfig {

	private String serverAddr;
	private String namespace;
	private String cluster;

	@Bean(initMethod = "init")
	@ConditionalOnMissingBean
	@ConfigurationProperties(prefix = "spring.datasource")
	public DruidDataSource druidDataSource() {
		StaticLog.info("初始化-Seata-DruidDataSource");
		return new DruidDataSource();
	}

	@Primary
	@Bean
	@ConditionalOnMissingBean
	public DataSourceProxy dataSource(DataSource dataSource) {
		return new DataSourceProxy(dataSource);
	}

	@Bean
	@ConditionalOnClass(Feign.class)
	public TxSeataFeignProxy TxSeataProxy() {
		return new TxSeataFeignProxy();
	}
}
