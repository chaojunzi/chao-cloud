package com.chao.cloud.common.extra.sharding.plugin;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.apache.shardingsphere.shardingjdbc.jdbc.core.datasource.ShardingDataSource;

import com.baomidou.mybatisplus.annotation.DbType;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.map.multi.SetValueMap;
import cn.hutool.core.util.StrUtil;
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
	 * 表结构ddl缓存
	 */
	Map<String, WeakReference<String>> TABLE_DDL_CACHE = new ConcurrentHashMap<>();
	/**
	 * (已经存在)表:->节点映射 <br>
	 * 例如：t_invoice_create:[t_invoice_create_202101]
	 */
	SetValueMap<String, String> TBALE_NODES_MAP = new SetValueMap<>(new ConcurrentHashMap<>());
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
					// 缓存表节点
					TBALE_NODES_MAP.put(sourceTable, CollUtil.newHashSet(nodes));
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
				TBALE_NODES_MAP.putValue(sourceTable, targetTable);
				StaticLog.info("表创建成功:table={}.{}", targetDsName, targetTable);
			} catch (Exception e) {
				StaticLog.error(e, "表创建失败:table={}.{}", targetDsName, targetTable);
				throw ExceptionUtil.wrapRuntime(e);
			}
		});

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
				nodes.add(t);
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

}
