package com.chao.cloud.common.extra.mybatis.sharding;

import org.apache.ibatis.mapping.SqlCommandType;

/**
 * 动态表名处理器
 *
 * @author miemie
 * @since 3.4.0
 */
public interface ShardingTableHandler {

	/**
	 * 生成动态表名
	 *
	 * @param type      操作类型
	 * @param sql       当前执行 SQL
	 * @param tableName 表名
	 * @param parameter 所有参数
	 * @return String 真实的表名
	 */
	String dynamicTableName(SqlCommandType type, String sql, String tableName, Object parameter);
}
