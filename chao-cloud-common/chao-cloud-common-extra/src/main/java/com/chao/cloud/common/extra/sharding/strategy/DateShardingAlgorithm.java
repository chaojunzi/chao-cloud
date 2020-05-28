package com.chao.cloud.common.extra.sharding.strategy;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.shardingsphere.api.sharding.complex.ComplexKeysShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.complex.ComplexKeysShardingValue;

import com.chao.cloud.common.extra.sharding.annotation.ShardingConfig;
import com.google.common.collect.Range;

import cn.hutool.core.lang.Assert;
import lombok.extern.slf4j.Slf4j;

/**
 * 根据日期分片-根据时间按照年月分片
 * 
 * @author 薛超
 * @since 2020年5月28日
 * @version 1.0.9
 */
@Slf4j
public class DateShardingAlgorithm implements ComplexKeysShardingAlgorithm<Date> {

	@Override
	public Collection<String> doSharding(Collection<String> tableNames,
			ComplexKeysShardingValue<Date> complexKeysShardingValue) {
		List<String> tableList = null;
		log.info("自定义按照日期进行分表");
		String table = complexKeysShardingValue.getLogicTableName();
		// 根据表获取相关字段
		String column = ShardingConfig.TABLE_COLUMN_MAP.get(table);
		Assert.notBlank(column, "table:{}, 未设置DateShardingAlgorithm分表", table);
		// 日期精确解析(= in)
		Map<String, Collection<Date>> shardingMap = complexKeysShardingValue.getColumnNameAndShardingValuesMap();
		if (shardingMap.containsKey(column)) {
			tableList = ShardingConfig.DATE_STRATEGY.findTables(tableNames, shardingMap.get(column));
		}
		// 范围日期解析
		Map<String, Range<Date>> rangeMap = complexKeysShardingValue.getColumnNameAndRangeValuesMap();
		if (rangeMap.containsKey(column)) {
			Range<Date> range = rangeMap.get(column);
			Date start = null, end = null;
			if (range.hasLowerBound()) {
				start = range.lowerEndpoint();
			}
			if (range.hasUpperBound()) {
				end = range.upperEndpoint();
			}
			tableList = ShardingConfig.DATE_STRATEGY.findTables(tableNames, start, end);
		}
		return tableList;
	}

}