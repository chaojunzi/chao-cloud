package com.chao.cloud.common.extra.sharding.enums;

/**
 * 分片算法枚举
 * 
 * @author 薛超
 * @since 2022年6月7日
 * @version 1.0.0
 */
public enum ShardingAlgorithmTypeEnum {
	/**
	 * sharding_code 切分数据源
	 */
	DS_SHARDING_CODE,
	/**
	 * hint
	 */
	DS_HINT,
	/**
	 * 根据字段取余
	 */
	DS_PART,
	/**
	 * 根据日期分表
	 */
	TABLE_DATE;

}
