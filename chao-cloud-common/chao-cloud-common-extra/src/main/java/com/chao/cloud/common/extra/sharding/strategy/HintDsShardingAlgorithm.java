package com.chao.cloud.common.extra.sharding.strategy;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.shardingsphere.api.sharding.hint.HintShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.hint.HintShardingValue;

import com.chao.cloud.common.extra.sharding.annotation.ShardingExtraConfig;
import com.chao.cloud.common.extra.sharding.annotation.ShardingProperties;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 根据外部字段进行分库<br>
 * 暂时只支持 shardingCode 处理
 * 
 * @author 薛超
 * @since 2020年12月1日
 * @version 1.0.0
 */
@Slf4j
public class HintDsShardingAlgorithm implements HintShardingAlgorithm<String> {

	@Override
	public Collection<String> doSharding(Collection<String> dsList, HintShardingValue<String> shardingValue) {
		// 获取配置信息
		ShardingProperties prop = SpringUtil.getBean(ShardingProperties.class);
		// 获取表名
		String table = shardingValue.getLogicTableName();
		// 获取传进来的值
		Collection<String> vals = shardingValue.getValues();
		log.info("Hint:自定义分片;tableName={};shardingValue={}", table, CollUtil.join(vals, StrUtil.COMMA));
		// 返回数据源
		Set<String> dsResult = new HashSet<>();
		//
		if (CollUtil.isNotEmpty(vals)) {
			for (String v : vals) {
				String ds = SpringUtil.getBean(ShardingExtraConfig.class).getDsByColumnValue(v);
				if (StrUtil.isNotBlank(ds)) {
					dsResult.add(ds);
				}
			}
		}
		if (CollUtil.isEmpty(dsResult)) {
			dsResult.add(prop.getDefaultDsName());// 默认数据源
		}
		return dsResult;
	}

}