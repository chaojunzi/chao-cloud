package com.chao.cloud.common.extra.mybatis.plugin;

import java.util.List;

import com.baomidou.mybatisplus.annotation.DbType;

/**
 * 自动补充表节点
 * 
 * @author 薛超
 * @since 2021年12月21日
 * @version 1.0.0
 */
public interface TableNodesComplete {
	/**
	 * 动态数据源类型
	 */
	String DYNAMIC_DS_TYPE = "com.baomidou.dynamic.datasource.DynamicRoutingDataSource";

	/**
	 * 获取数据源类型
	 * 
	 * @return 类型
	 */
	DbType getDbType();

	/**
	 * 获取该表全部节点
	 */
	List<String> getTableNodes(String table);

	/**
	 * 生成表节点
	 * 
	 * @param table            基础表
	 * @param targetTableNodes 真实表
	 */
	void createTableNode(String table, List<String> targetTableNodes);

}
