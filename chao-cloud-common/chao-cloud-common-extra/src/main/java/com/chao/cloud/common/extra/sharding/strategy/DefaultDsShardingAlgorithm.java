package com.chao.cloud.common.extra.sharding.strategy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.shardingsphere.api.sharding.complex.ComplexKeysShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.complex.ComplexKeysShardingValue;

import com.chao.cloud.common.extra.sharding.annotation.ShardingConfig;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 默认分库策略
 * 
 * @author 薛超
 * @since 2020年5月28日
 * @version 1.0.9
 */
@Slf4j
public class DefaultDsShardingAlgorithm implements ComplexKeysShardingAlgorithm<String> {
	// 分库字段-目前只支持一个
	public static volatile String SHARDING_COLUMN = StrUtil.EMPTY;

	@Override
	public Collection<String> doSharding(Collection<String> collection,
			ComplexKeysShardingValue<String> complexKeysShardingValue) {
		// 获取表名
		String table = complexKeysShardingValue.getLogicTableName();
		log.info("自定义按照{}={}进行分片", table, SHARDING_COLUMN);
		List<String> dsList = new ArrayList<>();
		// 获取分表字段及字段值
		Map<String, Collection<String>> map = complexKeysShardingValue.getColumnNameAndShardingValuesMap();
		Collection<String> vals = map.get(SHARDING_COLUMN);
		if (CollUtil.isNotEmpty(vals)) {
			for (String v : vals) {
				String ds = ShardingConfig.getDsByColumn(v);
				if (StrUtil.isNotBlank(ds)) {
					dsList.add(ds);
				}
			}
		}
		return dsList;
	}

}