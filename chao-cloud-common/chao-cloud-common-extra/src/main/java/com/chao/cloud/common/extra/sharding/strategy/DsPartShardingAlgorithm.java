package com.chao.cloud.common.extra.sharding.strategy;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.shardingsphere.api.sharding.complex.ComplexKeysShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.complex.ComplexKeysShardingValue;

import com.chao.cloud.common.extra.sharding.annotation.ShardingProperties;
import com.google.common.collect.Range;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 根据字段取余分库策略
 * 
 * @author 薛超
 * @since 2020年7月1日
 * @version 1.0.0
 */
@Slf4j
public class DsPartShardingAlgorithm implements ComplexKeysShardingAlgorithm<String> {

	@Override
	public Collection<String> doSharding(Collection<String> dsList,
			ComplexKeysShardingValue<String> complexKeysShardingValue) {
		ShardingProperties prop = SpringUtil.getBean(ShardingProperties.class);
		// 获取表名
		String table = complexKeysShardingValue.getLogicTableName();
		Set<String> dsSet = new HashSet<>();
		// 精确解析(= in)
		Map<String, Collection<String>> columnValMap = complexKeysShardingValue.getColumnNameAndShardingValuesMap();
		CollUtil.forEach(columnValMap, (c, valList, i) -> {
			CollUtil.removeBlank(valList);
			if (CollUtil.isEmpty(valList)) {
				return;
			}
			// 匹配数据源
			valList.forEach(v -> {
				addDsSet(dsSet, table, c, v);
			});
		});
		// 范围日期解析
		Map<String, Range<String>> rangeMap = complexKeysShardingValue.getColumnNameAndRangeValuesMap();
		CollUtil.forEach(rangeMap, (c, range, i) -> {
			if (range.hasLowerBound()) {
				addDsSet(dsSet, table, c, range.lowerEndpoint());
			}
			if (range.hasUpperBound()) {
				addDsSet(dsSet, table, c, range.upperEndpoint());
			}
		});
		// 设置默认数据源
		if (CollUtil.isEmpty(dsSet)) {
			dsSet.add(prop.getDefaultDsName());// 默认数据源
		}
		// 去掉不符合规则的数据源
		dsList = CollUtil.intersection(dsList, dsSet);
		log.info("【取余-{}】【{}】", table, CollUtil.join(dsList, StrUtil.COMMA));
		return dsList;
	}

	private void addDsSet(Set<String> dsSet, String table, String column, String val) {
		if (StrUtil.isBlank(val) || !NumberUtil.isNumber(val)) {
			return;
		}
		long number = NumberUtil.parseLong(val);
		ShardingProperties prop = SpringUtil.getBean(ShardingProperties.class);
		long index = number % prop.getDsNum();
		String dsName = StrUtil.format("{}{}", prop.getDsPrefix(), index);
		log.info("【取余】【{}%{}={}】{}.{}.{}={}", number, prop.getDsNum(), index, dsName, table, column, val);
		dsSet.add(dsName);
	}

}
