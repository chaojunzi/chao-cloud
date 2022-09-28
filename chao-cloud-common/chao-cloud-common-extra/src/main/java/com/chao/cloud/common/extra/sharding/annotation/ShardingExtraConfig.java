package com.chao.cloud.common.extra.sharding.annotation;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.apache.shardingsphere.driver.jdbc.core.datasource.ShardingSphereDataSource;
import org.apache.shardingsphere.infra.util.expr.InlineExpressionParser;
import org.apache.shardingsphere.infra.yaml.config.pojo.algorithm.YamlAlgorithmConfiguration;
import org.apache.shardingsphere.mode.manager.ContextManager;
import org.apache.shardingsphere.sharding.spring.boot.rule.YamlShardingRuleSpringBootConfiguration;
import org.apache.shardingsphere.sharding.yaml.config.YamlShardingRuleConfiguration;
import org.apache.shardingsphere.sharding.yaml.config.rule.YamlTableRuleConfiguration;
import org.apache.shardingsphere.sharding.yaml.config.strategy.sharding.YamlComplexShardingStrategyConfiguration;
import org.apache.shardingsphere.sharding.yaml.config.strategy.sharding.YamlShardingStrategyConfiguration;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.chao.cloud.common.extra.sharding.constant.ShardingConstant;
import com.chao.cloud.common.extra.sharding.plugin.TableActualNodesComplete;
import com.chao.cloud.common.util.EntityUtil;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;
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
@AutoConfigureAfter(SpringBootConfiguration.class)
@ConditionalOnProperty(prefix = ShardingConstant.SHARDING_PREFIX, name = "enable", havingValue = "true", matchIfMissing = true)
public class ShardingExtraConfig implements InitializingBean {
	// 列值：数据源；例如：A01->ds0 由转化ShardingProperties.dsColumnMap 反向转化而来
	private final Map<String, String> columnValueDsMap = new ConcurrentHashMap<>();

	@Autowired
	private YamlShardingRuleSpringBootConfiguration ruleProp;

	@Autowired(required = false)
	private ShardingSphereDataSource shardingDataSource;

	@Bean
	@ConditionalOnMissingBean(TableActualNodesComplete.class)
	public TableActualNodesComplete TableActualNodesComplete() {
		return new TableActualNodesComplete() {
		};
	}

	/**
	 * 获取ds
	 * 
	 * @param columnValue 字段值
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
		String defaultDsName = prop.getDefaultDsName();
		Assert.notBlank(defaultDsName, "chao.cloud.sharding.default-ds-name 不能为空");
		ContextManager contextManager = EntityUtil.getProperty(shardingDataSource, ContextManager.class);
		String databaseName = EntityUtil.getProperty(shardingDataSource, String.class);
		Map<String, DataSource> dsMap = contextManager.getDataSourceMap(databaseName);
		Assert.state(dsMap.containsKey(defaultDsName), "请设置默认的数据源:{}", defaultDsName);
		// 计算数据源总数
		List<String> dsList = new InlineExpressionParser(prop.getDsExps()).splitAndEvaluate();
		prop.setDsNum(CollUtil.size(dsList));
		// 获取默认分库策略
		YamlShardingRuleConfiguration shardingConfig = ruleProp.getSharding();
		YamlShardingStrategyConfiguration dds = shardingConfig.getDefaultDatabaseStrategy();
		if (dds != null && dds.getComplex() != null) {
			YamlComplexShardingStrategyConfiguration complex = dds.getComplex();
			String columns = complex.getShardingColumns();
			Assert.notBlank(columns, "Complex 默认分库列不能为空");
			// 获取第一个字段
			prop.setDsShardingColumn(StrUtil.split(columns, StrUtil.COMMA).get(0));
			// 构造columnValueDsMap->用于分库时匹配
			prop.getDsColumnMap().forEach((ds, list) -> {
				list.forEach(l -> columnValueDsMap.put(l, ds));
			});
		}
		// 解析规则
		Map<String, YamlTableRuleConfiguration> tables = shardingConfig.getTables();
		// 分片算法汇总
		Map<String, YamlAlgorithmConfiguration> shardingAlgorithms = shardingConfig.getShardingAlgorithms();
		// 填装日期对应策略
		Map<String, String> dateTableColumnMap = prop.getDateTableColumnMap();
		tables.forEach((t, r) -> {
			// 获取字段
			YamlShardingStrategyConfiguration tableStrategy = r.getTableStrategy();
			if (tableStrategy == null) {
				return;
			}
			YamlComplexShardingStrategyConfiguration complex = tableStrategy.getComplex();
			//
			if (complex == null) {
				log.warn("table: {} 无complex配置，不参与自定义日期解析");
				return;
			}
			boolean d = TableActualNodesComplete.isDateSharding(complex.getShardingAlgorithmName(), shardingAlgorithms);
			if (!d) {
				log.warn("table: {} 非tableDate配置，不参与自定义日期解析");
				return;
			}
			String shardingColumns = complex.getShardingColumns();// 此处是单字段解析
			dateTableColumnMap.put(t, shardingColumns);
			// 缓存算法
			String algorithmName = complex.getShardingAlgorithmName();
			ShardingConstant.putTableAlgorithm(t, algorithmName);
		});
		// 补全表节点
		if (prop.isCompleteTableNodes() && shardingDataSource != null && MapUtil.isNotEmpty(tables)) {
			// 解析数据表结构
			Map<String, List<String>> tableNodes = MapUtil.newHashMap();
			tables.forEach((t, r) -> {
				List<String> nodes = new InlineExpressionParser(r.getActualDataNodes()).splitAndEvaluate();
				nodes = nodes.stream().filter(n -> StrUtil.startWith(n, prop.getDsPrefix()))
						.collect(Collectors.toList());
				if (CollUtil.isNotEmpty(nodes)) {
					tableNodes.put(t, nodes);
				}
			});
			// 补全
			TableActualNodesComplete complete = SpringUtil.getBean(TableActualNodesComplete.class);
			complete.sourceOfTableName(shardingDataSource, tableNodes, defaultDsName);
		}
	}

}
