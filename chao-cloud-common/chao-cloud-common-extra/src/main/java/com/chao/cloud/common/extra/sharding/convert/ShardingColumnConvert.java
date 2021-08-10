package com.chao.cloud.common.extra.sharding.convert;

/**
 * 分片字段转化
 * 
 * @author 薛超
 * @since 2020年11月30日
 * @version 1.0.0
 */
@FunctionalInterface
public interface ShardingColumnConvert {

	/**
	 * 获取shardingCode
	 * 
	 * @param orgCode 开票网点编码
	 * @return {@link ShardingColumnModel}
	 */
	ShardingColumnModel getShardingColumnModel(String orgCode);

	static ShardingColumnModel buildShardingColumnModel(String orgCode) {
		return ShardingColumnModel.of()//
				.setOrgCode(orgCode);// 开票网点编码
	}

}
