package com.chao.cloud.common.extra.mybatis.dynamic;

import java.util.List;
import java.util.Map;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceProperties;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.chao.cloud.common.extra.mybatis.dynamic.DynamicTableRuleProperties.ShardingTableRule;
import com.chao.cloud.common.extra.mybatis.dynamic.DynamicTableRuleProperties.TableRule;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 数据源动态代理
 *
 * @author 薛超
 * @since 2021年9月23日
 * @version 1.0.0
 */
@Slf4j
public class DynamicDataSourceInterceptor implements MethodInterceptor, InitializingBean {

	@Autowired
	private DynamicTableRuleProperties prop;
	@Autowired
	private DynamicDataSourceProperties properties;

	private Map<String, String> tableDataSourceMap = MapUtil.newHashMap();

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		try {
			IService<?> service = (IService<?>) invocation.getThis();
			String dsName = getDsName(service);
			log.info("【当前数据源】: {}", dsName);
			DynamicDataSourceContextHolder.push(dsName);
			// 执行
			return invocation.proceed();
		} finally {
			DynamicDataSourceContextHolder.poll();
		}
	}

	private String getDsName(IService<?> service) {
		Class<?> entityClass = service.getEntityClass();
		TableInfo tableInfo = TableInfoHelper.getTableInfo(entityClass);
		String tableName = tableInfo.getTableName();
		if (tableDataSourceMap.containsKey(tableName)) {
			return tableDataSourceMap.get(tableName);
		}
		return properties.getPrimary();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Map<String, TableRule> datasource = prop.getDatasource();
		datasource.forEach((k, v) -> {
			List<String> tables = v.getTables();
			// 基础表名
			if (CollUtil.isNotEmpty(tables)) {
				tables.forEach(t -> tableDataSourceMap.put(t, k));
			}
			// 分片表
			Map<String, ShardingTableRule> shardingTableRule = v.getShardingTableRule();
			if (MapUtil.isNotEmpty(shardingTableRule)) {
				shardingTableRule.keySet().forEach(t -> tableDataSourceMap.put(t, k));
			}
		});
	}

}
