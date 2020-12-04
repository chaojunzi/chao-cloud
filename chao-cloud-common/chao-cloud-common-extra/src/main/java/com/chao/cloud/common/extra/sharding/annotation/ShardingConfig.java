package com.chao.cloud.common.extra.sharding.annotation;

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
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.chao.cloud.common.extra.sharding.convert.HintShardingColumnProxy;
import com.chao.cloud.common.extra.sharding.convert.ShardingColumnConvert;
import com.chao.cloud.common.extra.sharding.strategy.DateShardingAlgorithm;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 分库分表配置
 * 
 * @author 薛超
 * @since 2020年12月1日
 * @version 1.0.0
 */
@Slf4j
@Configuration
public class ShardingConfig implements InitializingBean {
	// 列值：数据源；例如：A01->ds0 由转化ShardingProperties.dsColumnMap 反向转化而来
	private final Map<String, String> columnValueDsMap = new ConcurrentHashMap<>();

	@Autowired
	private SpringBootShardingRuleConfigurationProperties ruleProp;

	/**
	 * 获取ds
	 * 
	 * @param column 字段值 例：A01A02
	 * @return 数据源名称
	 */
	public String getDsByColumnValue(String columnValue) {
		if (StrUtil.isBlank(columnValue)) {
			return null;
		}
		Set<Entry<String, String>> entries = columnValueDsMap.entrySet();
		for (Entry<String, String> entry : entries) {
			// 正则表达式
			// if (ReUtil.isMatch(entry.getKey(), column)) {
			// 前缀匹配
			if (columnValue.startsWith(entry.getKey())) {
				return entry.getValue();
			}
		}
		return null;

	}

	@Override
	public void afterPropertiesSet() throws Exception {
		ShardingProperties prop = SpringUtil.getBean(ShardingProperties.class);
		// 默认数据源name
		String dsName = ruleProp.getDefaultDataSourceName();
		Assert.notBlank(dsName, "spring.shardingsphere.sharding.defaultDataSourceName 不能为空");
		prop.setDefaultDsName(dsName);
		// 获取默认分库策略
		YamlShardingStrategyConfiguration dds = ruleProp.getDefaultDatabaseStrategy();
		if (dds != null && dds.getComplex() != null) {
			YamlComplexShardingStrategyConfiguration complex = dds.getComplex();
			String columns = complex.getShardingColumns();
			Assert.notBlank(columns, "Complex 默认分库列不能为空");
			// 获取第一个字段
			prop.setDsShardingColumn(StrUtil.split(columns, StrUtil.COMMA)[0]);
			// 构造columnValueDsMap->用于分库时匹配
			prop.getDsColumnMap().forEach((ds, list) -> {
				list.forEach(l -> columnValueDsMap.put(l, ds));
			});
		}
		// 解析规则
		Map<String, YamlTableRuleConfiguration> tables = ruleProp.getTables();
		// 填装日期对应策略
		Map<String, String> dateTableColumnMap = prop.getDateTableColumnMap();
		tables.forEach((t, r) -> {
			// 获取字段
			YamlShardingStrategyConfiguration tableStrategy = r.getTableStrategy();
			if (tableStrategy == null) {
				return;
			}
			YamlComplexShardingStrategyConfiguration complex = tableStrategy.getComplex();
			if (complex == null || !DateShardingAlgorithm.class.getName().equals(complex.getAlgorithmClassName())) {
				log.warn("table: {} 无complex配置，不参与自定义日期解析");
				return;
			}
			String shardingColumns = complex.getShardingColumns();// 此处是单字段解析
			dateTableColumnMap.put(t, shardingColumns);
		});
	}

	@Bean
	@ConditionalOnMissingBean(SpringUtil.class)
	SpringUtil springUtil() {
		return new SpringUtil();
	}

	@Bean
	ShardingProperties shardingProperties() {
		return new ShardingProperties();
	}

	@Bean
	@ConditionalOnMissingBean(ShardingColumnConvert.class)
	ShardingColumnConvert shardingColumnConvert() {
		// 返回原字段
		return code -> code;
	}

	@Bean
	@ConditionalOnMissingBean(HintShardingColumnProxy.class)
	HintShardingColumnProxy hintShardingColumnProxy(ShardingColumnConvert convert) {
		return new HintShardingColumnProxy(convert);
	}
}
