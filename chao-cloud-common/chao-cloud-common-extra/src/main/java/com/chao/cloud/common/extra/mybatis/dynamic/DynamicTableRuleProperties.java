package com.chao.cloud.common.extra.mybatis.dynamic;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DataSourceProperty;
import com.baomidou.mybatisplus.annotation.DbType;
import com.chao.cloud.common.extra.mybatis.common.DBConstant;
import com.chao.cloud.common.extra.mybatis.common.DateStrategyEnum;
import com.chao.cloud.common.extra.mybatis.common.ShardEnum;

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
public class DynamicTableRuleProperties {

	/**
	 * 每一个数据源
	 */
	private Map<String, TableRule> datasource = new LinkedHashMap<>();

	private int order = 0;

	/**
	 * 参考{@link DataSourceProperty}
	 */
	@Data
	public static class TableRule {
		/**
		 * 数据源类型
		 */
		private DbType dbType = DbType.SQLITE;
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
		 * 逻辑表
		 */
		private List<String> tables;
		/**
		 * 动态表名
		 */
		private Map<String, ShardingTableRule> shardingTableRule;
	}

	@Data
	public static class ShardingTableRule {
		/**
		 * 数据源类型
		 */
		private DbType dbType = DbType.SQLITE;
		/**
		 * 分片类型（默认不分片）
		 */
		private ShardEnum type = ShardEnum.NONE;
		/**
		 * 分片字段（目前只支持单个字段）
		 */
		private String column;
		/**
		 * 日期策略
		 */
		private DateStrategyEnum dateStrategy = DateStrategyEnum.MONTH_12;
	}
}
