package com.chao.cloud.common.extra.sharding.plugin;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.apache.shardingsphere.shardingjdbc.jdbc.core.datasource.ShardingDataSource;

import com.baomidou.mybatisplus.annotation.DbType;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.Db;
import cn.hutool.db.DbUtil;
import cn.hutool.db.meta.MetaUtil;
import cn.hutool.log.StaticLog;

/**
 * 自动补全表节点
 * 
 * @author 薛超
 * @since 2021年5月21日
 * @version 1.0.9
 */
public interface TableActualNodesComplete {
	/**
	 * 表定义语言
	 */
	String CREATE_TABLE = "CREATE TABLE";
	String SHOW_CREATE_TABLE = "SHOW " + CREATE_TABLE + " {};";

	/**
	 * 根据表名生成表结构<br>
	 * 格式： table:ds.table_202001
	 * 
	 * @param shardingDataSource 数据源
	 * @param tableNodes         源表_节点
	 */
	default void sourceOfTableName(ShardingDataSource shardingDataSource, Map<String, List<String>> tableNodes) {
		DbType type = DbType.getDbType(shardingDataSource.getDatabaseType().getName());
		switch (type) {
		case MYSQL:// 目前只支持mysql
			Map<String, DataSource> dsMap = shardingDataSource.getDataSourceMap();
			// 节点分组
			Map<String, Map<String, List<String>>> dsTableNodes = build(tableNodes);
			// 生成表结构
			dsTableNodes.forEach((dsName, tNodesMap) -> {
				DataSource ds = dsMap.get(dsName);
				List<String> tables = MetaUtil.getTables(ds);
				// 生成数据表
				tNodesMap.forEach((st, nodes) -> {
					// 需要生成的表
					Collection<String> needTables = CollUtil.subtract(nodes, tables);
					needTables.forEach(nt -> createTable(ds, st, nt));
				});
			});
		default:
			break;
		}
	}

	static void createTable(DataSource ds, String sourceTable, String targetTable) {
		try {
			Db db = DbUtil.use(ds);
			// 获取表定义语言ddl
			String createTableSql = db.query(StrUtil.format(SHOW_CREATE_TABLE, sourceTable), //
					// 获取第二列 Create Table
					rs -> rs.next() ? rs.getString(2) : null);
			// 生成新的表
			createTableSql = createTableSql.replaceFirst("(?i)" + sourceTable, targetTable)//
					.replaceFirst("(?i)" + CREATE_TABLE, CREATE_TABLE + " IF NOT EXISTS");
			// 创建
			db.execute(createTableSql);
			StaticLog.info("表创建成功：table={},来源于[{}]", targetTable, sourceTable);
		} catch (Exception e) {
			StaticLog.error(e, "表创建失败：table={}", targetTable);
			throw ExceptionUtil.wrapRuntime(e);
		}

	}

	/**
	 * 节点分组
	 * 
	 * @param tableNodes 表节点
	 */
	static Map<String, Map<String, List<String>>> build(Map<String, List<String>> tableNodes) {
		List<DsTableNodes> tableList = CollUtil.newArrayList();
		tableNodes.forEach((t, n) -> {
			Map<String, List<String>> dsTables = n.stream().collect(//
					Collectors.groupingBy(s -> StrUtil.split(s, StrUtil.DOT).get(0), // key
							Collectors.mapping(s -> CollUtil.getLast(StrUtil.split(s, StrUtil.DOT)), //
									Collectors.toList())));
			// 构造节点信息
			dsTables.forEach((ds, nodes) -> {
				DsTableNodes dtn = DsTableNodes.of().setDsName(ds);
				dtn.getTableNodes().put(t, nodes);// 设置表节点
				tableList.add(dtn);
			});
		});
		// 根据ds合并value
		return tableList.stream().collect(Collectors.toMap(//
				DsTableNodes::getDsName, //
				DsTableNodes::getTableNodes, //
				(t1, t2) -> {
					t1.putAll(t2);
					return t1;
				}));
	}

}
