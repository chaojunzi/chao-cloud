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
	 * @param code 转换为shardingCode 处理
	 * @return shardingCode
	 */
	String getShardingCode(String code);

}
