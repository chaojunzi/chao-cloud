package com.chao.cloud.common.extra.sharding.strategy;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.shardingsphere.api.sharding.complex.ComplexKeysShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.complex.ComplexKeysShardingValue;
import org.apache.shardingsphere.shardingjdbc.jdbc.core.datasource.ShardingDataSource;

import com.chao.cloud.common.extra.mybatis.common.DateStrategyEnum;
import com.chao.cloud.common.extra.sharding.annotation.ShardingProperties;
import com.chao.cloud.common.extra.sharding.constant.ShardingConstant;
import com.chao.cloud.common.extra.sharding.plugin.ShardingActualNodesComplete;
import com.google.common.collect.Range;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 根据日期分片-根据时间按照年月分片
 * 
 * @author 薛超
 * @since 2020年5月29日
 * @version 1.0.0
 */
@Slf4j
public class DateShardingAlgorithm implements ComplexKeysShardingAlgorithm<Date> {

	@Override
	public Collection<String> doSharding(Collection<String> tableNames,
			ComplexKeysShardingValue<Date> complexKeysShardingValue) {
		// 获取配置文件
		ShardingProperties prop = SpringUtil.getBean(ShardingProperties.class);
		String table = complexKeysShardingValue.getLogicTableName();
		// 日期分表策略
		DateStrategyEnum dateStrategy = prop.getDateStrategy();
		//
		List<String> tableList = null;
		// 根据表获取相关字段
		String columnName = prop.getDateTableColumnMap().get(table);
		Assert.notBlank(columnName, "table:{}, 未设置DateShardingAlgorithm分表", table);
		// 日期精确解析(= in)
		Map<String, Collection<Date>> shardingMap = complexKeysShardingValue.getColumnNameAndShardingValuesMap();
		if (shardingMap.containsKey(columnName)) {
			// 获取时间范围
			Collection<Date> dateList = shardingMap.get(columnName);
			// 生成表节点
			this.createTableNodes(prop, table, tableNames, dateStrategy, dateList);
			//
			tableList = dateStrategy.findTables(tableNames, dateList);
		}
		// 范围日期解析
		Map<String, Range<Date>> rangeMap = complexKeysShardingValue.getColumnNameAndRangeValuesMap();
		if (rangeMap.containsKey(columnName)) {
			Range<Date> range = rangeMap.get(columnName);
			Date start = null, end = null;
			if (range.hasLowerBound()) {
				start = range.lowerEndpoint();
			}
			if (range.hasUpperBound()) {
				end = range.upperEndpoint();
			}
			tableList = dateStrategy.findTables(tableNames, start, end);
		}
		if (CollUtil.isEmpty(tableList)) {
			String tableName = CollUtil.getFirst(tableNames);
			// 空表
			tableList = CollUtil.toList(ShardingConstant.getTableNameOfNumberSuffix(tableName));
		}
		// 打印
		log.info("【日期】【{}】Table={}.{}", CollUtil.join(tableList, StrUtil.COMMA), table, columnName);
		return tableList;
	}

	/**
	 * 生成表节点
	 * 
	 * @param prop         配置
	 * @param sourceTable  逻辑表
	 * @param tableNames   现有的所有表节点
	 * @param dateStrategy 日期策略
	 * @param dateList     时间范围
	 */
	private synchronized void createTableNodes(ShardingProperties prop, String sourceTable,
			Collection<String> tableNames, DateStrategyEnum dateStrategy, Collection<Date> dateList) {
		if (!prop.isCompleteTableNodes()) {
			return;
		}
		// 只有insert才会生成表节点（只有一个值）
		if (CollUtil.size(dateList) != 1) {
			return;
		}
		// 获取当前节点-判断时间
		String currNode = dateStrategy.getCurrentTableNode(sourceTable, CollUtil.getFirst(dateList));
		if (StrUtil.isBlank(currNode) || tableNames.contains(currNode)) {
			return;
		}
		// 生成表->获取全部数据源
		Map<String, DataSource> dsMap = SpringUtil.getBean(ShardingDataSource.class).getDataSourceMap();
		// 获取表结构
		String sourceTableDDL = ShardingActualNodesComplete.getSourceTableDDL(dsMap.get(prop.getDefaultDsName()),
				sourceTable);
		// 要生成的节点
		List<String> targetTables = CollUtil.toList(currNode);
		dsMap.forEach((dsName, ds) -> {
			if (!StrUtil.startWith(dsName, prop.getDsPrefix())) {
				return;
			}
			try {
				ShardingActualNodesComplete.createTable(sourceTable, sourceTableDDL, dsName, ds, targetTables);
			} catch (Exception e) {
				// 生成表结构失败
				throw e;
			}
		});
	}

}
