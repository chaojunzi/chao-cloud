package com.chao.cloud.common.extra.mybatis.common;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import com.baomidou.mybatisplus.core.plugins.InterceptorIgnoreHelper;
import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import com.baomidou.mybatisplus.core.toolkit.TableNameParser;
import com.baomidou.mybatisplus.extension.plugins.inner.ShardingTableInnerInterceptor;

import lombok.RequiredArgsConstructor;

/**
 * 分片拦截器
 * 
 * @author 薛超
 * @since 2021年12月20日
 * @version 1.0.0
 */
@RequiredArgsConstructor
@SuppressWarnings({ "rawtypes" })
public class MyShardTableInterceptor extends ShardingTableInnerInterceptor {

	private final Map<String, ShardTableHandler> shardingTableHandlerMap;

	@Override
	public void beforeQuery(Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds,
			ResultHandler resultHandler, BoundSql boundSql) throws SQLException {
		PluginUtils.MPBoundSql mpBs = PluginUtils.mpBoundSql(boundSql);
		if (InterceptorIgnoreHelper.willIgnoreDynamicTableName(ms.getId())) {
			return;
		}
		String targetTable = this.changeTable(ms.getSqlCommandType(), mpBs.sql(), parameter);
		mpBs.sql(targetTable);
	}

	@Override
	public void beforePrepare(StatementHandler sh, Connection connection, Integer transactionTimeout) {
		PluginUtils.MPStatementHandler mpSh = PluginUtils.mpStatementHandler(sh);
		MappedStatement ms = mpSh.mappedStatement();
		SqlCommandType sct = ms.getSqlCommandType();
		if (sct == SqlCommandType.INSERT || sct == SqlCommandType.UPDATE || sct == SqlCommandType.DELETE) {
			if (InterceptorIgnoreHelper.willIgnoreDynamicTableName(ms.getId()))
				return;
			PluginUtils.MPBoundSql mpBs = mpSh.mPBoundSql();
			Object parameter = mpSh.parameterHandler().getParameterObject();
			mpBs.sql(this.changeTable(sct, mpBs.sql(), parameter));
		}
	}

	protected String changeTable(SqlCommandType type, String sql, Object parameter) {
		TableNameParser parser = new TableNameParser(sql);
		List<TableNameParser.SqlToken> names = new ArrayList<>();
		parser.accept(names::add);
		StringBuilder builder = new StringBuilder();
		int last = 0;
		for (TableNameParser.SqlToken name : names) {
			int start = name.getStart();
			if (start != last) {
				builder.append(sql, last, start);
				String table = name.getValue();
				ShardTableHandler handler = shardingTableHandlerMap.get(table);
				if (handler != null) {
					// 解析参数
					builder.append(handler.dynamicTableName(type, sql, table, parameter));
				} else {
					builder.append(table);
				}
			}
			last = name.getEnd();
		}
		if (last != sql.length()) {
			builder.append(sql.substring(last));
		}
		return builder.toString();
	}

}
