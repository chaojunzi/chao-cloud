package com.chao.cloud.common.extra.sharding.algorithm;

import java.util.Collection;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.apache.shardingsphere.sharding.api.sharding.hint.HintShardingAlgorithm;
import org.apache.shardingsphere.sharding.api.sharding.hint.HintShardingValue;
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
 * 根据外部字段进行分库<br>
 * 暂时只支持 shardingCode 处理
 * 
 * @author 薛超
 * @since 2020年12月1日
 * @version 1.0.0
 */
@Slf4j
public class DsHintShardingAlgorithm implements HintShardingAlgorithm<String> {

	@Getter
	private Properties props;

	@Autowired
	private ShardingProperties prop;
	@Autowired
	@Lazy
	private ShardingExtraConfig shardingExtraConfig;

	@Override
	public Collection<String> doSharding(Collection<String> dsList, HintShardingValue<String> shardingValue) {
		// 获取表名
		String table = shardingValue.getLogicTableName();
		// 获取传进来的值
		Collection<String> vals = shardingValue.getValues();
		// 返回数据源
		Set<String> dsResult = new HashSet<>();
		//
		if (CollUtil.isNotEmpty(vals)) {
			for (String v : vals) {
				String ds = shardingExtraConfig.getDsByColumnValue(v);
				if (StrUtil.isNotBlank(ds)) {
					dsResult.add(ds);
				}
			}
		}
		if (CollUtil.isEmpty(dsResult)) {
			dsResult.add(prop.getDefaultDsName());// 默认数据源
		}
		log.info("【Hint-{}】: {}.{}", CollUtil.join(vals, StrUtil.COMMA), CollUtil.join(dsResult, StrUtil.COMMA), table);
		return dsResult;
	}

	@Override
	public String getType() {
		return ShardingAlgorithmTypeEnum.DS_HINT.name();
	}

	@Override
	public void init(Properties props) {
		this.props = props;
	}

}