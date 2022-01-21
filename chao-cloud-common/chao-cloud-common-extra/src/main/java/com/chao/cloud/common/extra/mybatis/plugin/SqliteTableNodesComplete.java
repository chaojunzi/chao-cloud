package com.chao.cloud.common.extra.mybatis.plugin;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.annotation.DbType;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.map.multi.SetValueMap;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.DbUtil;
import cn.hutool.db.meta.MetaUtil;
import cn.hutool.db.meta.TableType;
import lombok.extern.slf4j.Slf4j;

/**
 * sqlite 生成表节点
 * 
 * @author 薛超
 * @since 2021年12月21日
 * @version 1.0.0
 */
@Slf4j
@Component
public class SqliteTableNodesComplete implements TableNodesComplete {

	@Autowired
	private DataSource dataSource;// 数据源

	private static final SetValueMap<String, String> tableNodeMap = new SetValueMap<>(new ConcurrentHashMap<>());

	private final String ddlSql = "SELECT sql from sqlite_master WHERE type='table' AND name='{}'";
	private final String indexSql = "SELECT sql from sqlite_master WHERE type='index' AND tbl_name ='{}'";

	@Override
	public DbType getDbType() {
		return DbType.SQLITE;
	}

	@Override
	public List<String> getTableNodes(String table) {
		Set<String> nodes = tableNodeMap.getOrDefault(table, new HashSet<>());
		if (CollUtil.isEmpty(nodes)) {
			try {
				// 读取表
				List<String> tables = MetaUtil.getTables(dataSource, TableType.TABLE);
				CollUtil.filter(tables, t -> StrUtil.startWith(t, table));
				nodes.addAll(tables);
			} catch (Exception e) {
				throw ExceptionUtil.wrapRuntime(e);
			}
		}
		tableNodeMap.put(table, nodes);
		return CollUtil.newArrayList(nodes);
	}

	@Override
	public void createTableNode(String table, List<String> targetTableNodes) {
		// 去重
		if (CollUtil.isEmpty(targetTableNodes)) {
			return;
		}
		Set<String> nodes = tableNodeMap.getOrDefault(table, new HashSet<>());
		CollUtil.filter(targetTableNodes, n -> !nodes.contains(n));
		if (CollUtil.isEmpty(targetTableNodes)) {
			return;
		}
		try {
			// 获取表信息（表结构-索引）
			String ddlSql = StrUtil.format(this.ddlSql, table);
			log.info("【查询表结构】: {}", ddlSql);
			String ddl = DbUtil.use(dataSource).queryString(ddlSql);
			// 获取索引
			String indexSql = StrUtil.format(this.indexSql, table);
			log.info("【查询表索引】: {}", indexSql);
			List<String> indexList = DbUtil.use(dataSource).query(indexSql, String.class);
			log.info("[index size]: {}", CollUtil.size(indexList));
			// 生成表
			targetTableNodes.forEach(n -> this.createTable(table, n, ddl, indexList));
		} catch (Exception e) {
			throw ExceptionUtil.wrapRuntime(e);
		}

	}

	private void createTable(String sourceTable, String targetTable, String ddl, List<String> indexList) {
		// 生成表结构
		String ddlSql = ddl.replaceFirst("(?i)" + sourceTable, targetTable);
		// 生成表
		try {
			log.info("【生成表】: {}", ddlSql);
			int i = DbUtil.use(dataSource).execute(ddlSql);
			log.info("[total]: {}", i);
			tableNodeMap.putValue(sourceTable, targetTable);
			if (CollUtil.isEmpty(indexList)) {
				return;
			}
			// 生成索引
			indexList.forEach(index -> {
				try {
					// 索引名称大写
					String indexSql = index.replaceFirst("(?i)" + sourceTable, targetTable.toUpperCase());
					log.info("【生成索引】: {}", indexSql);
					int j = DbUtil.use(dataSource).execute(indexSql);
					log.info("[total]: {}", j);
				} catch (Exception e) {
					throw ExceptionUtil.wrapRuntime(e);
				}
			});
		} catch (Exception e) {
			if (StrUtil.contains(e.getMessage(), "already exists")) {
				tableNodeMap.putValue(sourceTable, targetTable);
				return;
			}
			throw ExceptionUtil.wrapRuntime(e);
		}
	}

}
