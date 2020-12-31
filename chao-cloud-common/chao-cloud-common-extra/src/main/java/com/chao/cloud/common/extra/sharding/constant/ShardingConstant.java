package com.chao.cloud.common.extra.sharding.constant;

/**
 * sharding常量参数
 * 
 * @author 薛超
 * @since 2020年12月2日
 * @version 1.0.0
 */
public interface ShardingConstant {

	String SHARDING_PREFIX = "chao.cloud.sharding";

	/**
	 * 分片代理
	 */
	String SHARDING_PROXY = "@annotation(com.chao.cloud.common.extra.sharding.annotation.ShardingColumn)";

	/**
	 * 分库标识
	 */
	String SHARDING_CODE = "sharding_code";
	/**
	 * 默认值
	 */
	String DEFAULT_VALUE = "0000";

}
