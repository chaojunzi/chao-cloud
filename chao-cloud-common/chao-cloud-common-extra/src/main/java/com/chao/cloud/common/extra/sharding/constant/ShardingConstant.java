package com.chao.cloud.common.extra.sharding.constant;

import java.util.Map;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ReUtil;

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
	/**
	 * 表->算法 映射
	 */
	Map<String, String> TABLE_ALGORITHM_MAP = MapUtil.newHashMap();

	/**
	 * 设置算法
	 */
	static void putTableAlgorithm(String tableName, String algorithmType) {
		TABLE_ALGORITHM_MAP.put(tableName, algorithmType);
	}

	static String getTableAlgorithm(String tableName) {
		return TABLE_ALGORITHM_MAP.get(tableName);
	}

	/**
	 * 获取数字后缀的原表名
	 * 
	 * @param fullTableName 表全名
	 * @return 基础表名
	 */
	static String getTableNameOfNumberSuffix(String fullTableName) {
		return ReUtil.delLast("_\\d+", fullTableName);
	}

}
