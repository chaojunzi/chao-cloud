package com.chao.cloud.common.extra.sharding.strategy;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.shardingsphere.api.sharding.complex.ComplexKeysShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.complex.ComplexKeysShardingValue;

import com.chao.cloud.common.extra.sharding.annotation.ShardingProperties;
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
		DateStrategyEnum dateStrategy = prop.getDateStrategy();
		//
		List<String> tableList = null;
		String table = complexKeysShardingValue.getLogicTableName();
		// 根据表获取相关字段
		String columnName = prop.getDateTableColumnMap().get(table);
		Assert.notBlank(columnName, "table:{}, 未设置DateShardingAlgorithm分表", table);
		log.info("Complex:Table:自定义分片;tableName={};columnName={}", table, columnName);
		// 日期精确解析(= in)
		Map<String, Collection<Date>> shardingMap = complexKeysShardingValue.getColumnNameAndShardingValuesMap();
		if (shardingMap.containsKey(columnName)) {
			tableList = dateStrategy.findTables(tableNames, shardingMap.get(columnName));
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
			tableList = CollUtil.toList(
					StrUtil.subPre(tableName, tableName.length() - DateStrategyEnum.MONTH_PATTERN.length() - 1));
		}
		return tableList;
	}

}
