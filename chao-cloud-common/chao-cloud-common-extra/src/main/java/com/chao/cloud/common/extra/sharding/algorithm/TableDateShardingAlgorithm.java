package com.chao.cloud.common.extra.sharding.algorithm;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.shardingsphere.driver.jdbc.core.datasource.ShardingSphereDataSource;
import org.apache.shardingsphere.mode.manager.ContextManager;
import org.apache.shardingsphere.sharding.api.sharding.complex.ComplexKeysShardingAlgorithm;
import org.apache.shardingsphere.sharding.api.sharding.complex.ComplexKeysShardingValue;
import org.springframework.beans.factory.annotation.Autowired;

import com.chao.cloud.common.extra.sharding.annotation.ShardingProperties;
import com.chao.cloud.common.extra.sharding.constant.ShardingConstant;
import com.chao.cloud.common.extra.sharding.enums.DateStrategyEnum;
import com.chao.cloud.common.extra.sharding.enums.ShardingAlgorithmTypeEnum;
import com.chao.cloud.common.extra.sharding.plugin.TableActualNodesComplete;
import com.chao.cloud.common.util.EntityUtil;
import com.google.common.collect.Range;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateException;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 根据日期分片-根据时间按照年月分片
 * 
 * @author 薛超
 * @since 2020年5月29日
 * @version 1.0.0
 */
@Slf4j
public class TableDateShardingAlgorithm implements ComplexKeysShardingAlgorithm<Comparable<?>> {
	@Getter
	private Properties props;
	@Autowired
	private ShardingProperties prop;

	@Override
	public Collection<String> doSharding(Collection<String> tableNames,
			ComplexKeysShardingValue<Comparable<?>> complexKeysShardingValue) {
		// 获取配置文件
		String table = complexKeysShardingValue.getLogicTableName();
		// 日期分表策略
		DateStrategyEnum dateStrategy = prop.getDateStrategy();
		//
		List<String> tableList = null;
		// 根据表获取相关字段
		String columnName = prop.getDateTableColumnMap().get(table);
		Assert.notBlank(columnName, "table:{}, 未设置DateShardingAlgorithm分表", table);
		// 日期精确解析(= in)
		Map<String, Collection<Comparable<?>>> shardingMap = complexKeysShardingValue
				.getColumnNameAndShardingValuesMap();
		if (shardingMap.containsKey(columnName)) {
			// 获取时间范围
			Collection<Comparable<?>> dateObjs = shardingMap.get(columnName);
			List<Date> dateList = CollUtil.map(dateObjs, this::toDate, true);
			// 生成表节点
			this.createTableNodes(prop, table, tableNames, dateStrategy, dateList);
			//
			tableList = dateStrategy.findTables(tableNames, dateList);
		}
		// 范围日期解析
		Map<String, Range<Comparable<?>>> rangeMap = complexKeysShardingValue.getColumnNameAndRangeValuesMap();
		if (rangeMap.containsKey(columnName)) {
			Range<Comparable<?>> range = rangeMap.get(columnName);
			Date start = null, end = null;
			if (range.hasLowerBound()) {
				start = toDate(range.lowerEndpoint());
			}
			if (range.hasUpperBound()) {
				end = toDate(range.upperEndpoint());
			}
			tableList = dateStrategy.findTables(tableNames, start, end);
		}
		if (CollUtil.isEmpty(tableList)) {
			String tableName = CollUtil.getFirst(tableNames);
			// 空表
			tableList = CollUtil.toList(ShardingConstant.getTableNameOfNumberSuffix(tableName));
		}
		// 打印
		log.info("【日期】[{}] => [{}] ", columnName, CollUtil.join(tableList, StrUtil.COMMA));
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
		ShardingSphereDataSource dataSource = SpringUtil.getBean(ShardingSphereDataSource.class);
		String databaseName = EntityUtil.getProperty(dataSource, String.class);
		Map<String, DataSource> dsMap = EntityUtil.getProperty(dataSource, ContextManager.class)
				.getDataSourceMap(databaseName);
		// 获取表结构
		String sourceTableDDL = TableActualNodesComplete.getSourceTableDDL(dsMap.get(prop.getDefaultDsName()),
				sourceTable);
		// 要生成的节点
		List<String> targetTables = CollUtil.toList(currNode);
		dsMap.forEach((dsName, ds) -> {
			if (!StrUtil.startWith(dsName, prop.getDsPrefix())) {
				return;
			}
			try {
				TableActualNodesComplete.createTable(sourceTable, sourceTableDDL, dsName, ds, targetTables);
			} catch (Exception e) {
				// 生成表结构失败
				throw e;
			}
		});
	}

	public Date toDate(Comparable<?> dateObj) {
		if (dateObj == null) {
			return null;
		}
		if (dateObj instanceof Date) {
			return (Date) dateObj;
		}
		String dateStr = StrUtil.toString(dateObj);
		if (StrUtil.isBlank(dateStr)) {
			return null;
		}
		try {
			//
			return DateUtil.parse(dateStr);
		} catch (DateException e) {
			if (NumberUtil.isLong(dateStr)) {
				log.warn("【日期为非通用格式】: [{}]，将尝试为long类型进行转换！", dateStr);
				return DateUtil.date(NumberUtil.parseLong(dateStr));
			}
			throw e;
		}
	}

	@Override
	public String getType() {
		return ShardingAlgorithmTypeEnum.TABLE_DATE.name();
	}

	@Override
	public void init(Properties props) {
		this.props = props;
	}
}
