package com.chao.cloud.common.extra.mybatis.config;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.chao.cloud.common.extra.mybatis.common.DBConstant;

import lombok.Data;

/**
 * 动态表节点配置
 * 
 * @author 薛超
 * @since 2021年9月23日
 * @version 1.0.0
 */
@Data
@ConfigurationProperties(prefix = DBConstant.DYNAMIC_CONFIG_PREFIX)
public class DynamicTableProperties {
	/**
	 * 默认不启用
	 */
	private boolean enabled;
	/**
	 * 必须设置默认的库,默认master
	 */
	private String primary = "master";
	/**
	 * 每一个数据源
	 */
	private Map<String, DynamicRule> datasource = new LinkedHashMap<>();
	/**
	 * aop执行顺序
	 */
	private int order = 0;

	/**
	 * 参考 @{link
	 * com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DataSourceProperty}
	 */
	@Data
	public static class DynamicRule {
		/**
		 * 连接池名称(只是一个名称标识)</br>
		 * 默认是配置文件上的名称
		 */
		private String poolName;
		/**
		 * 连接池类型，如果不设置自动查找 Druid > HikariCp
		 */
		private Class<? extends DataSource> type;
		/**
		 * JDBC driver
		 */
		private String driverClassName;
		/**
		 * JDBC url 地址
		 */
		private String url;
		/**
		 * JDBC 用户名
		 */
		private String username;
		/**
		 * JDBC 密码
		 */
		private String password;
		/**
		 * lazy init datasource
		 */
		private Boolean lazy;
		/**
		 * 是否隐藏->用于页面展示
		 */
		private boolean hide;
		/**
		 * 逻辑表
		 */
		private List<String> tables;

	}

}
