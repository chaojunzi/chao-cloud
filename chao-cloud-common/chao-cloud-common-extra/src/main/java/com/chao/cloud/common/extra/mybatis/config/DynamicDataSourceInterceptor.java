package com.chao.cloud.common.extra.mybatis.config;

import java.util.List;
import java.util.Map;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.chao.cloud.common.extra.mybatis.config.DynamicTableProperties.DynamicRule;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
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
	private DynamicTableProperties prop;

	private Map<String, String> tableDataSourceMap = MapUtil.newHashMap();

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		if (!prop.isEnabled()) {
			return invocation.proceed();
		}
		IService<?> service = (IService<?>) invocation.getThis();
		String dsName = getDsName(service);
		if (StrUtil.isBlank(dsName)) {
			// 默认数据源
			return invocation.proceed();
		}
		try {
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
		return null;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Map<String, DynamicRule> datasource = prop.getDatasource();
		datasource.forEach((k, v) -> {
			List<String> tables = v.getTables();
			// 基础表名
			if (CollUtil.isNotEmpty(tables)) {
				tables.forEach(t -> tableDataSourceMap.put(t, k));
			}
		});
	}
}
