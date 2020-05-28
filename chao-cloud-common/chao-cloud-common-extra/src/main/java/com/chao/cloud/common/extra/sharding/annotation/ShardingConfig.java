package com.chao.cloud.common.extra.sharding.annotation;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.shardingsphere.core.yaml.config.sharding.YamlShardingStrategyConfiguration;
import org.apache.shardingsphere.core.yaml.config.sharding.YamlTableRuleConfiguration;
import org.apache.shardingsphere.core.yaml.config.sharding.strategy.YamlComplexShardingStrategyConfiguration;
import org.apache.shardingsphere.shardingjdbc.spring.boot.sharding.SpringBootShardingRuleConfigurationProperties;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.chao.cloud.common.extra.sharding.strategy.DateShardingAlgorithm;
import com.chao.cloud.common.extra.sharding.strategy.DateStrategyEnum;
import com.chao.cloud.common.extra.sharding.strategy.DefaultDsShardingAlgorithm;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * 分库分表配置
 * 
 * @author 薛超
 * @since 2020年5月28日
 * @version 1.0.9
 */
@Configuration
@ConfigurationProperties(prefix = "chao.cloud.sharding")
@Data
@Slf4j
public class ShardingConfig implements InitializingBean {

	public static volatile Map<String, String> COLUMN_DS_MAP = new ConcurrentHashMap<>();
	// 这里只解析table-单字段
	public static volatile Map<String, String> TABLE_COLUMN_MAP = new ConcurrentHashMap<>();
	//
	public static volatile DateStrategyEnum DATE_STRATEGY = DateStrategyEnum.MONTH_12;

	private Map<String, String> dsColumnMap = new HashMap<>();
	// 日期策略：默认12个月
	private DateStrategyEnum dateStrategy;

	@Autowired
	private SpringBootShardingRuleConfigurationProperties ruleProp;

	/**
	 * 获取ds
	 * 
	 * @param column 字段值
	 * @return 数据源名称
	 */
	public static String getDsByColumn(String column) {
		Set<Entry<String, String>> entries = COLUMN_DS_MAP.entrySet();
		for (Entry<String, String> entry : entries) {
			if (entry.getKey().contains(column)) {
				return entry.getValue();
			}
		}
		return null;

	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// 获取默认分库策略
		YamlShardingStrategyConfiguration dds = ruleProp.getDefaultDatabaseStrategy();
		if (dds != null && dds.getComplex() != null) {
			YamlComplexShardingStrategyConfiguration complex = dds.getComplex();
			String columns = complex.getShardingColumns();
			Assert.notBlank(columns, "Complex 默认分库列不能为空");
			// 获取第一个字段
			DefaultDsShardingAlgorithm.SHARDING_COLUMN = StrUtil.split(columns, StrUtil.COMMA)[0];
			COLUMN_DS_MAP = MapUtil.reverse(dsColumnMap);
		}
		// 解析规则
		Map<String, YamlTableRuleConfiguration> tables = ruleProp.getTables();
		tables.forEach((t, r) -> {
			// 获取字段
			YamlComplexShardingStrategyConfiguration complex = r.getTableStrategy().getComplex();
			if (complex == null || !DateShardingAlgorithm.class.getName().equals(complex.getAlgorithmClassName())) {
				log.warn("table: {} 无complex配置，不参与自定义日期解析");
				return;
			}
			String shardingColumns = complex.getShardingColumns();
			TABLE_COLUMN_MAP.put(t, StrUtil.split(shardingColumns, StrUtil.COMMA)[0]);
			// 日期策略
			if (dateStrategy != null) {
				DATE_STRATEGY = dateStrategy;
			}
		});
	}

}
