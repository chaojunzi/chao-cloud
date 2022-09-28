package com.chao.cloud.common.extra.sharding.algorithm;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.shardingsphere.sharding.api.sharding.complex.ComplexKeysShardingAlgorithm;
import org.apache.shardingsphere.sharding.api.sharding.complex.ComplexKeysShardingValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import com.chao.cloud.common.extra.sharding.annotation.ShardingExtraConfig;
import com.chao.cloud.common.extra.sharding.annotation.ShardingProperties;
import com.chao.cloud.common.extra.sharding.enums.ShardingAlgorithmTypeEnum;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 默认分库策略
 * 
 * @author 薛超
 * @since 2020年5月29日
 * @version 1.0.0
 */
@Slf4j
public class DsShardingCodeShardingAlgorithm implements ComplexKeysShardingAlgorithm<String> {
	@Getter
	private Properties props;
	@Autowired
	private ShardingProperties prop;
	@Autowired
	@Lazy
	private ShardingExtraConfig shardingExtraConfig;

	@Override
	public Collection<String> doSharding(Collection<String> dsList,
			ComplexKeysShardingValue<String> complexKeysShardingValue) {
		String dsShardingColumn = prop.getDsShardingColumn();
		// 获取表名
		String table = complexKeysShardingValue.getLogicTableName();
		Set<String> dsSet = new HashSet<>();
		// 获取分库字段及字段值
		Map<String, Collection<String>> map = complexKeysShardingValue.getColumnNameAndShardingValuesMap();
		Collection<String> vals = map.get(dsShardingColumn);
		if (CollUtil.isNotEmpty(vals)) {
			for (String v : vals) {
				String ds = shardingExtraConfig.getDsByColumnValue(v);
				if (StrUtil.isNotBlank(ds)) {
					dsSet.add(ds);
				}
			}
		}
		if (CollUtil.isEmpty(dsSet)) {
			dsSet.add(prop.getDefaultDsName());// 默认数据源
		}
		log.info("【DS:default】【{}】{}.{}=[{}]", CollUtil.join(dsSet, StrUtil.COMMA), table, dsShardingColumn,
				CollUtil.join(vals, StrUtil.COMMA));
		return dsSet;
	}

	@Override
	public String getType() {
		return ShardingAlgorithmTypeEnum.DS_SHARDING_CODE.name();
	}

	@Override
	public void init(Properties props) {
		this.props = props;
	}

}
