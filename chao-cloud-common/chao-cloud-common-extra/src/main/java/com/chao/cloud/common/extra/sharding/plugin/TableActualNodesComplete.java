package com.chao.cloud.common.extra.sharding.plugin;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.apache.shardingsphere.core.rule.TableRule;
import org.apache.shardingsphere.shardingjdbc.jdbc.core.datasource.ShardingDataSource;
import org.apache.shardingsphere.underlying.common.rule.DataNode;

import com.baomidou.mybatisplus.annotation.DbType;
import com.chao.cloud.common.extra.sharding.constant.ShardingConstant;
import com.chao.cloud.common.extra.sharding.strategy.DateShardingAlgorithm;
import com.chao.cloud.common.util.EntityUtil;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.DbUtil;
import cn.hutool.db.meta.MetaUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.log.StaticLog;

/**
 * 自动补全表节点
 * 
 * @author 薛超
 * @since 2021年8月23日
 * @version 1.0.0
 */
public interface TableActualNodesComplete {

	/**
	 * 表结构ddl缓存
	 */
	Map<String, WeakReference<String>> TABLE_DDL_CACHE = new ConcurrentHashMap<>();
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
	 * @param defaultDsName      默认数据源
	 */
	default void sourceOfTableName(ShardingDataSource shardingDataSource, Map<String, List<String>> tableNodes,
			String defaultDsName) {
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
				tNodesMap.forEach((sourceTable, nodes) -> {
					// 整合需要缓存表节点
					cacheTableNodes(dsName, sourceTable, nodes, tables);
					// 需要生成的表
					Collection<String> needTables = CollUtil.subtract(nodes, tables);
					// 生成表节点
					if (CollUtil.isEmpty(needTables)) {
						return;
					}
					String sourceTableDDL = getSourceTableDDL(dsMap.get(defaultDsName), sourceTable);
					createTable(sourceTable, sourceTableDDL, dsName, ds, needTables);
				});
			});
		default:
			break;
		}
	}

	/**
	 * 缓存表节点
	 * 
	 * @param dsName      数据源名称
	 * @param tableName   逻辑表名称
	 * @param nodes       配置的表节点
	 * @param existTables 已经存在的所有的表
	 */
	default void cacheTableNodes(String dsName, String tableName, List<String> nodes, List<String> existTables) {
		// 获取算法
		Class<?> tableAlgorithm = ShardingConstant.getTableAlgorithm(tableName);
		// 日期算法
		if (tableAlgorithm == DateShardingAlgorithm.class) {
			// 过滤此表的所有已存在的表结点
			Collection<String> existNodes = CollUtil.filterNew(existTables,
					t -> StrUtil.equals(tableName, ShardingConstant.getTableNameOfNumberSuffix(t)));
			if (CollUtil.isNotEmpty(nodes)) {
				nodes.addAll(existNodes);
			}
		}
		// 刷新配置信息
		refreshDatasource(dsName, tableName, nodes);

	}

	/**
	 * 生成表
	 * 
	 * @param sourceTable    基础表名
	 * @param sourceTableDDL 表结构ddl
	 * @param targetDsName   目标数据据源名称
	 * @param targetDs       目标数据源
	 * @param targetTables   目标表
	 */
	static void createTable(String sourceTable, String sourceTableDDL, String targetDsName, DataSource targetDs,
			Collection<String> targetTables) {
		// 生成新的表
		CollUtil.forEach(targetTables, (targetTable, i) -> {
			String createTableSql = sourceTableDDL.replaceFirst("(?i)" + sourceTable, targetTable)//
					.replaceFirst("(?i)" + CREATE_TABLE, CREATE_TABLE + " IF NOT EXISTS");
			StaticLog.info("【DDL】【{}.{}】\n{}", targetDsName, targetTable, createTableSql);
			// 目标数据源创建
			try {
				DbUtil.use(targetDs).execute(createTableSql);
				StaticLog.info("表创建成功:table={}.{}", targetDsName, targetTable);
			} catch (Exception e) {
				StaticLog.error(e, "表创建失败:table={}.{}", targetDsName, targetTable);
				throw ExceptionUtil.wrapRuntime(e);
			}
		});
		// 刷新数据源
		refreshDatasource(targetDsName, sourceTable, targetTables);

	}

	static String getSourceTableDDL(DataSource sourceDs, String sourceTable) {
		return Optional.ofNullable(TABLE_DDL_CACHE.get(sourceTable)).map(WeakReference::get)//
				.orElseGet(() -> {
					try {
						// 获取表定义语言ddl
						String tableDDL = DbUtil.use(sourceDs).query(StrUtil.format(SHOW_CREATE_TABLE, sourceTable), //
								// 获取第二列 Create Table
								rs -> rs.next() ? rs.getString(2) : null);
						TABLE_DDL_CACHE.put(sourceTable, new WeakReference<>(tableDDL));
						return tableDDL;
					} catch (Exception e) {
						StaticLog.error(e, "获取表失败：sourceTable={}", sourceTable);
						throw ExceptionUtil.wrapRuntime(e);
					}
				});

	}

	/**
	 * 节点分组
	 * 
	 * @param tableNodes 表节点
	 * @return 数据源:{逻辑表:[真实表]}
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
				// 加入基础表节点
				if (!CollUtil.contains(nodes, t)) {
					nodes.add(t);
				}
				//
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

	/**
	 * 动态刷新数据源
	 * 
	 * @param dsName    数据源名称
	 * @param tableName 逻辑表
	 * @param nodes     真实表
	 */
	/**
	 * 动态刷新数据源
	 */
	static void refreshDatasource(String dsName, String tableName, Collection<String> nodes) {
		ShardingDataSource shardingDataSource = SpringUtil.getBean(ShardingDataSource.class);
		TableRule tableRule = shardingDataSource.getRuntimeContext().getRule().getTableRule(tableName);
		// 1.动态刷新：actualDataNodes
		List<DataNode> actualDataNodes = tableRule.getActualDataNodes();
		nodes.forEach(n -> {
			DataNode node = new DataNode(dsName, n);
			if (!CollUtil.contains(actualDataNodes, node)) {
				actualDataNodes.add(node);
			}
		});
		//
		Set<String> actualTables = Sets.newHashSet();
		Map<DataNode, Integer> dataNodeIndexMap = Maps.newHashMap();
		// 修改节点索引
		CollUtil.forEach(actualDataNodes, (n, i) -> {
			actualTables.add(n.getTableName());
			dataNodeIndexMap.put(n, i);
		});
		EntityUtil.setProperty(tableRule, TableRule::getActualDataNodes, actualDataNodes);
		// 2.动态刷新：actualTables
		EntityUtil.setProperty(tableRule, new TypeReference<Set<String>>() {
		}, actualTables);
		// 3.动态刷新：dataNodeIndexMap
		EntityUtil.setProperty(tableRule, new TypeReference<Map<DataNode, Integer>>() {
		}, dataNodeIndexMap);
		// 4.动态刷新：datasourceToTablesMap
		Map<String, Collection<String>> datasourceToTablesMap = EntityUtil.getProperty(tableRule,
				new TypeReference<Map<String, Collection<String>>>() {
				});
		if (datasourceToTablesMap.containsKey(dsName)) {
			Collection<String> tables = datasourceToTablesMap.get(dsName);
			tables.addAll(nodes);
		} else {
			datasourceToTablesMap.put(dsName, actualTables);
		}
	}
}
