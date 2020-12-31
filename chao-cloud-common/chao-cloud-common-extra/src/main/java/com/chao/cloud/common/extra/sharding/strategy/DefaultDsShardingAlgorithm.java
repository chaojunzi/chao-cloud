package com.chao.cloud.common.extra.sharding.strategy;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.shardingsphere.api.sharding.complex.ComplexKeysShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.complex.ComplexKeysShardingValue;

import com.chao.cloud.common.extra.sharding.annotation.ShardingExtraConfig;
import com.chao.cloud.common.extra.sharding.annotation.ShardingProperties;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 默认分库策略
 * 
 * @author 薛超
 * @since 2020年5月29日
 * @version 1.0.0
 */
@Slf4j
public class DefaultDsShardingAlgorithm implements ComplexKeysShardingAlgorithm<String> {

	@Override
	public Collection<String> doSharding(Collection<String> collection,
			ComplexKeysShardingValue<String> complexKeysShardingValue) {
		ShardingProperties prop = SpringUtil.getBean(ShardingProperties.class);
		String dsShardingColumn = prop.getDsShardingColumn();
		// 获取表名
		String table = complexKeysShardingValue.getLogicTableName();
		Set<String> dsSet = new HashSet<>();
		// 获取分库字段及字段值
		Map<String, Collection<String>> map = complexKeysShardingValue.getColumnNameAndShardingValuesMap();
		Collection<String> vals = map.get(dsShardingColumn);
		log.info("Complex:DS:自定义分片;tableName={};columnName={},columnValue={}", table, dsShardingColumn,
				CollUtil.join(vals, StrUtil.COMMA));
		if (CollUtil.isNotEmpty(vals)) {
			for (String v : vals) {
				String ds = SpringUtil.getBean(ShardingExtraConfig.class).getDsByColumnValue(v);
				if (StrUtil.isNotBlank(ds)) {
					dsSet.add(ds);
				}
			}
		}
		if (CollUtil.isEmpty(dsSet)) {
			dsSet.add(prop.getDefaultDsName());// 默认数据源
		}
		return dsSet;
	}

}
