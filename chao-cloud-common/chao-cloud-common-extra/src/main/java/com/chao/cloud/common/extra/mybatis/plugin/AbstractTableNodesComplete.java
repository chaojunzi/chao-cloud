package com.chao.cloud.common.extra.mybatis.plugin;

import java.sql.DatabaseMetaData;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;

import com.chao.cloud.common.extra.mybatis.config.DynamicTableProperties;
import com.chao.cloud.common.extra.mybatis.config.DynamicTableProperties.DynamicRule;
import com.chao.cloud.common.extra.mybatis.util.MybatisUtil;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.map.multi.SetValueMap;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.meta.MetaUtil;
import cn.hutool.db.meta.TableType;

/**
 * 生成表节点
 * 
 * @author 薛超
 * @since 2022年5月27日
 * @version 1.0.0
 */
public abstract class AbstractTableNodesComplete implements TableNodesComplete {

	@Autowired
	protected DataSource dataSource;// 数据源
	@Autowired
	protected DynamicTableProperties properties;// 数据源

	static final SetValueMap<String, String> tableNodesMap = new SetValueMap<>(new ConcurrentHashMap<>());

	@Override
	public List<String> getTableNodes(String table) {
		Set<String> nodes = getNodes(table);
		if (CollUtil.isEmpty(nodes)) {
			try {
				// 读取表
				List<String> tables = MetaUtil.getTables(dataSource, TableType.TABLE);
				// StrUtil.startWith(t, table);
				CollUtil.filter(tables, t -> ReUtil.isMatch(table + "_?[\\d]*", t));
				nodes.addAll(tables);
				putNodes(table, nodes);
			} catch (Exception e) {
				throw ExceptionUtil.wrapRuntime(e);
			}
		}
		return CollUtil.newArrayList(nodes);
	}

	protected String getDsAlias() {
		// 动态
		if (StrUtil.equals(ClassUtil.getClassName(dataSource, false), DYNAMIC_DS_TYPE)) {
			try {
				DatabaseMetaData metaData = MybatisUtil.getMetaData(dataSource);
				String url = metaData.getURL();
				// 获取数据库名称
				Map<String, DynamicRule> ruleMap = properties.getDatasource();
				for (Entry<String, DynamicRule> entry : ruleMap.entrySet()) {
					DynamicRule value = entry.getValue();
					if (StrUtil.startWith(value.getUrl(), url)) {
						return entry.getKey();
					}
				}
				return properties.getPrimary();
			} catch (Exception e) {
				throw ExceptionUtil.wrapRuntime(e);
			}
		}
		// 单节点
		return "default";
	}

	protected String buildKey(String table) {
		String alias = getDsAlias();
		return StrUtil.format("{}.{}", alias, table);
	}

	protected Set<String> getNodes(String table) {
		Set<String> nodes = tableNodesMap.getOrDefault(buildKey(table), new HashSet<>());
		return nodes;
	}

	protected void putNodes(String table, Set<String> nodes) {
		tableNodesMap.put(buildKey(table), nodes);
	}

	protected void putNode(String table, String targetTable) {
		tableNodesMap.putValue(buildKey(table), targetTable);
	}

}
