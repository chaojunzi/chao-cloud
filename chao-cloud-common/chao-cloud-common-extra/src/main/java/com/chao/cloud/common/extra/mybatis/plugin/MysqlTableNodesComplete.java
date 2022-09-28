package com.chao.cloud.common.extra.mybatis.plugin;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.annotation.DbType;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.DbUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * mysql 生成表节点
 * 
 * @author 薛超
 * @since 2022年5月10日
 * @version 1.0.0
 */
@Slf4j
@Component
public class MysqlTableNodesComplete extends AbstractTableNodesComplete {

	/**
	 * 表结构ddl缓存
	 */
	private static final Map<String, WeakReference<String>> TABLE_DDL_CACHE = new ConcurrentHashMap<>();

	/**
	 * 表定义语言
	 */
	private static final String CREATE_TABLE = "CREATE TABLE";
	private static final String SHOW_CREATE_TABLE = "SHOW " + CREATE_TABLE + " {};";

	@Override
	public DbType getDbType() {
		return DbType.MYSQL;
	}

	@Override
	public void createTableNode(String table, List<String> targetTableNodes) {
		// 去重
		if (CollUtil.isEmpty(targetTableNodes)) {
			return;
		}
		Set<String> nodes = getNodes(table);
		CollUtil.filter(targetTableNodes, n -> !nodes.contains(n));
		if (CollUtil.isEmpty(targetTableNodes)) {
			return;
		}
		try {
			String ddl = getSourceTableDDL(dataSource, table);
			// 生成表
			String alias = getDsAlias();
			createTable(table, ddl, alias, dataSource, targetTableNodes);
			//
		} catch (Exception e) {
			throw ExceptionUtil.wrapRuntime(e);
		}

	}

	/**
	 * 生成表
	 * 
	 * @param sourceTable    基础表名
	 * @param sourceTableDDL 表结构ddl
	 * @param dsAlias        数据源别名
	 * @param targetDs       目标数据源
	 * @param targetTables   目标表
	 */
	private void createTable(String sourceTable, String sourceTableDDL, String dsAlias, DataSource targetDs,
			Collection<String> targetTables) {
		// 生成新的表
		CollUtil.forEach(targetTables, (targetTable, i) -> {
			String createTableSql = sourceTableDDL.replaceFirst("(?i)" + sourceTable, targetTable)//
					.replaceFirst("(?i)" + CREATE_TABLE, CREATE_TABLE + " IF NOT EXISTS");
			log.info("【DDL】【{}.{}】\n{}", dsAlias, targetTable, createTableSql);
			// 目标数据源创建
			try {
				DbUtil.use(targetDs).execute(createTableSql);
				log.info("表创建成功:table={}.{}", dsAlias, targetTable);
				putNode(sourceTable, targetTable);
			} catch (Exception e) {
				log.error("表创建失败:table={}.{}", dsAlias, targetTable, e);
				throw ExceptionUtil.wrapRuntime(e);
			}
		});

	}

	private String getSourceTableDDL(DataSource sourceDs, String sourceTable) {
		return Optional.ofNullable(TABLE_DDL_CACHE.get(sourceTable)).map(WeakReference::get)//
				.orElseGet(() -> {
					try {
						// 获取表定义语言ddl
						String tableDDL = DbUtil.use(sourceDs).query(StrUtil.format(SHOW_CREATE_TABLE, sourceTable), //
								// 获取第二列 Create Table
								rs -> rs.next() ? rs.getString(2) : null);
						// 删除表分区ddl语句
						tableDDL = ReUtil.delFirst("/\\*.*?\\*/", tableDDL);
						// 删除自动递增（归一）
						tableDDL = ReUtil.delFirst("AUTO_INCREMENT=(\\d+)", tableDDL);
						//
						TABLE_DDL_CACHE.put(sourceTable, new WeakReference<>(tableDDL));
						return tableDDL;
					} catch (Exception e) {
						log.error("获取表失败：sourceTable={}", sourceTable, e);
						throw ExceptionUtil.wrapRuntime(e);
					}
				});

	}

}
