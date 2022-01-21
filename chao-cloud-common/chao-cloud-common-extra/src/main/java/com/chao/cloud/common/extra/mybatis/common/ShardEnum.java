package com.chao.cloud.common.extra.mybatis.common;

import java.util.function.Function;

import com.chao.cloud.common.extra.mybatis.dynamic.DynamicTableRuleProperties.ShardingTableRule;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 分片枚举
 * 
 * @author 薛超
 * @since 2021年12月20日
 * @version 1.0.0
 */
@RequiredArgsConstructor
public enum ShardEnum {

	/**
	 * 默认不分片
	 */
	NONE(rule -> (type, sql, table, param) -> table),
	/**
	 * 根据日期分片
	 */
	DATE(rule -> new DateTableNameHandler(rule)),

	/**
	 * 请自行扩展...
	 */
	;@Getter
	private final Function<ShardingTableRule, ShardTableHandler> handlerFunc;

	public ShardTableHandler buildTableNameHandler(ShardingTableRule rule) {
		return handlerFunc.apply(rule);
	}
}