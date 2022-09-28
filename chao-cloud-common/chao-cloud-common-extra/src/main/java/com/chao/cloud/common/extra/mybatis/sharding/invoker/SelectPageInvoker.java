package com.chao.cloud.common.extra.mybatis.sharding.invoker;

import java.lang.reflect.Method;
import java.util.List;

import javax.sql.DataSource;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.annotation.Autowired;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.conditions.AbstractWrapper;
import com.baomidou.mybatisplus.core.conditions.ISqlSegment;
import com.baomidou.mybatisplus.core.conditions.segments.MergeSegments;
import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.override.MybatisMapperProxy;
import com.chao.cloud.common.extra.mybatis.config.ShardingTableProperties.ShardingTableRule;
import com.chao.cloud.common.extra.mybatis.constant.MapperEnum;
import com.chao.cloud.common.extra.mybatis.constant.MybatisConstant;
import com.chao.cloud.common.extra.mybatis.util.MybatisUtil;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 分页查询多条<br>
 * 1.{@link BaseMapper#selectMapsPage(com.baomidou.mybatisplus.core.metadata.IPage, com.baomidou.mybatisplus.core.conditions.Wrapper)}<br>
 * 2.{@link BaseMapper#selectPage(com.baomidou.mybatisplus.core.metadata.IPage, com.baomidou.mybatisplus.core.conditions.Wrapper)}
 * 
 * @author 薛超
 * @since 2022年5月19日
 * @version 1.0.0
 */
public class SelectPageInvoker extends AbstractMapperInvoker {

	static final Method countMethod = ReflectUtil.getMethodByName(BaseMapper.class, SqlMethod.SELECT_COUNT.getMethod());
	// list查询的两种方式
	static final Method listMethod = ReflectUtil.getMethodByName(BaseMapper.class, SqlMethod.SELECT_LIST.getMethod());
	static final Method mapsMethod = ReflectUtil.getMethodByName(BaseMapper.class, SqlMethod.SELECT_MAPS.getMethod());
	// 支持的数据库
	static final List<DbType> supportDbTypes = CollUtil.toList(DbType.MYSQL, DbType.SQLITE, DbType.H2, DbType.MARIADB,
			DbType.SQL_SERVER, DbType.SQL_SERVER2005);

	@Autowired
	private DataSource dataSource;

	@Override
	public MapperEnum getMapperMethod() {
		return MapperEnum.page;
	}

	@Override
	public <T> Object invoke(MybatisMapperProxy<T> mapperProxy, TableInfo tableInfo, ShardingTableRule rule,
			Object[] args, MethodInvocation invocation) throws Throwable {
		// 获取表
		List<String> tableNodes = super.findActualTables(tableInfo, rule, args);
		//
		if (CollUtil.size(tableNodes) <= 0) {
			return getFirstParam(IPage.class, args);
		}
		// 单表无需排序（或不支持的数据源）
		if (CollUtil.size(tableNodes) == 1) {
			return getOncePageResult(tableNodes, invocation);
		}
		DbType dbType = MybatisUtil.getDbType(dataSource);
		if (!CollUtil.contains(supportDbTypes, dbType)) {
			return getOncePageResult(tableNodes, invocation);
		}
		// 敬请期待...
		if (prop.isSupportPage()) {
			return page(mapperProxy, tableInfo, tableNodes, args, invocation);
		}
		return invocation.proceed();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private <T, E> IPage<?> page(MybatisMapperProxy<T> mapperProxy, TableInfo tableInfo, List<String> tableNodes,
			Object[] args, MethodInvocation invocation) throws Throwable {
		IPage<E> page = getFirstParam(IPage.class, args);
		long limit = page.getCurrent() * page.getSize();
		if (limit <= 0) {
			return page;
		}
		// 获取page参数
		AbstractWrapper wrapper = getFirstParam(AbstractWrapper.class, args);
		// 调用所有table的count方法
		List<CountInfo> countList = CollUtil.toList();
		for (String table : tableNodes) {
			Integer c = (Integer) execute(table, () -> {
				try {
					return mapperProxy.invoke(mapperProxy, countMethod, new Object[] { wrapper });
				} catch (Throwable e) {
					throw ExceptionUtil.wrapRuntime(e);
				}
			});
			//
			if (c > 0) {
				countList.add(CountInfo.of().setCount(c).setTableName(table));
			}
		}
		if (CollUtil.isEmpty(countList)) {
			return page;
		}
		List<String> lastTables = CollUtil.map(countList, CountInfo::getTableName, true);
		// 下面开始获取所有的数据集合
		int total = countList.stream().mapToInt(CountInfo::getCount).sum();
		page.setTotal(total);
		// 单表?
		boolean oneTable = CollUtil.size(lastTables) == 1;
		// 设置查询limit
		wrapper.last(StrUtil.format(MybatisConstant.LIMIT_TEMPLATE, //
				oneTable ? page.offset() : MybatisConstant.OFFSET_0, //
				oneTable ? page.getSize() : limit));
		String pageMethod = invocation.getMethod().getName();
		Method recordsMethod = StrUtil.startWith(pageMethod, mapsMethod.getName()) ? mapsMethod : listMethod;
		List<E> records = CollUtil.toList();
		for (String table : lastTables) {
			// 获取数据
			List<E> list = (List<E>) execute(table, () -> {
				try {
					return mapperProxy.invoke(mapperProxy, recordsMethod, new Object[] { wrapper });
				} catch (Throwable e) {
					throw ExceptionUtil.wrapRuntime(e);
				}
			});
			CollUtil.addAll(records, list);
		}
		// 多张表中的数据需要排序
		if (!oneTable) {
			MergeSegments expression = wrapper.getExpression();
			if (expression != null) {
				List<String> orderList = CollUtil.map(expression.getOrderBy(), ISqlSegment::getSqlSegment, true);
				// 排序
				sort(records, orderList, tableInfo);
			}
			// 分页
			int pageNo = (int) page.getCurrent();
			int size = (int) page.getSize();
			records = ListUtil.page(pageNo - 1, size, records);
		}
		page.setRecords(records);
		// 返回
		return page;
	}

	/**
	 * 查询一次分页
	 */
	private IPage<?> getOncePageResult(List<String> tableNodes, MethodInvocation invocation) throws Throwable {
		Class<?> returnType = invocation.getMethod().getReturnType();
		return (IPage<?>) executeQuery(tableNodes, invocation, returnType);
	}

	@Data(staticConstructor = "of")
	@Accessors(chain = true)
	public static class CountInfo {
		private String tableName;
		private int count;
	}

}
