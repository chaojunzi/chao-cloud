package com.chao.cloud.common.extra.mybatis.sharding.invoker;

import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import javax.sql.DataSource;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.annotation.Autowired;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.conditions.AbstractWrapper;
import com.baomidou.mybatisplus.core.enums.SqlKeyword;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.chao.cloud.common.extra.mybatis.common.DateStrategyEnum;
import com.chao.cloud.common.extra.mybatis.config.ShardingTableProperties;
import com.chao.cloud.common.extra.mybatis.config.ShardingTableProperties.ShardingTableRule;
import com.chao.cloud.common.extra.mybatis.plugin.TableNodesComplete;
import com.chao.cloud.common.extra.mybatis.sharding.DateRangeModel;
import com.chao.cloud.common.extra.mybatis.sharding.MapperInvoker;
import com.chao.cloud.common.extra.mybatis.util.MybatisUtil;
import com.chao.cloud.common.extra.mybatis.util.WrapperUtil;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Filter;
import cn.hutool.core.lang.func.Func0;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;

/**
 * 抽象方法
 * 
 * @author 薛超
 * @since 2022年5月18日
 * @version 1.0.0
 */
public abstract class AbstractMapperInvoker implements MapperInvoker {

	@Autowired
	protected ShardingTableProperties prop;

	public <T> T execute(String tableName, Func0<T> countFunc) {
		try {
			MapperInvoker.setTableName(tableName);
			return countFunc.callWithRuntimeException();
		} finally {
			MapperInvoker.remove();
		}
	}

	/**
	 * 方法 update/delete/count返回值为integer的方法
	 * 
	 * @param tableNodes 表节点
	 * @param exitFilter 是否退出循环
	 * @param invocation 代理类
	 * @return 结果
	 * @throws Throwable 异常信息
	 */
	public Integer execute(List<String> tableNodes, MethodInvocation invocation) throws Throwable {
		return execute(tableNodes, null, invocation);
	}

	public Integer execute(List<String> tableNodes, Filter<Integer> exitFilter, MethodInvocation invocation)
			throws Throwable {
		Integer r = 0;
		for (String node : tableNodes) {
			try {
				MapperInvoker.setTableName(node);
				Object obj = invocation.proceed();
				if (obj instanceof Integer) {
					r += (Integer) obj;
					if (exitFilter != null) {
						boolean exit = exitFilter.accept(r);
						if (exit) {
							break;
						}
					}
				}
			} finally {
				MapperInvoker.remove();
			}
		}
		return r;
	}

	public <T> Object executeQuery(List<String> tableNodes, MethodInvocation invocation, Class<?> returnType)
			throws Throwable {
		return executeQuery(tableNodes, null, invocation, returnType);
	}

	@SuppressWarnings("unchecked")
	public <T> Object executeQuery(List<String> tableNodes, Filter<Object> exitFilter, MethodInvocation invocation,
			Class<?> returnType) throws Throwable {
		Object r = null;
		// list集合
		boolean isList = ClassUtil.isAssignable(List.class, returnType);
		if (isList) {
			r = CollUtil.toList();
		}
		List<String> emptyNodes = CollUtil.toList();
		for (String node : tableNodes) {
			try {
				MapperInvoker.setTableName(node);
				Object obj = invocation.proceed();
				if (isList) {
					CollUtil.addAll((List<T>) r, obj);
					if (ObjectUtil.isEmpty(obj)) {
						emptyNodes.add(node);
					}
				} else {
					r = obj;
				}
				if (exitFilter != null) {
					boolean exit = exitFilter.accept(obj);
					if (exit) {
						break;
					}
				}
			} finally {
				MapperInvoker.remove();
			}
		}
		tableNodes.removeAll(emptyNodes);
		return r;
	}

	/**
	 * 获取真实表节点
	 * 
	 * @param tableInfo 表信息
	 * @param rule      分片规则
	 * @param args      参数列表
	 * @return 表节点
	 */
	public List<String> findActualTables(TableInfo tableInfo, ShardingTableRule rule, Object[] args) {
		String table = tableInfo.getTableName();
		// 获取表结构
		TableNodesComplete complete = getTableNodesComplete();
		// 全部表节点
		List<String> tableNodes = complete.getTableNodes(table);
		Assert.notEmpty(tableNodes, "【{}】: 未匹配到任意表节点，请创建表！", table);
		// 获取参数中的日期范围
		DateRangeModel model = this.buildDateRange(rule.getColumn(), tableInfo, args);
		// 获取表范围（精确查询范围）
		boolean exact = CollUtil.contains(WrapperUtil.exactSqlKeywords, model.getKeyword());
		DateStrategyEnum dateStrategy = rule.getDateStrategy();
		List<String> tableList = null;
		if (exact) {
			List<Date> dateList = model.getDateList();
			Assert.notEmpty(dateList, "【sql错误】: {}.{}#请传入有效日期~", table, rule.getColumn());
			tableList = dateStrategy.findTables(tableNodes, dateList);
		} else {
			tableList = dateStrategy.findTables(tableNodes, model.getLowerDate(), model.getUpperDate());
		}
		// 无表时，设置默认表
		// if (CollUtil.isEmpty(tableList)) {
		// tableList.add(table);
		// }
		// 倒叙-习惯于从后往前查（比如getOne，减少查询次数）
		return CollUtil.sort(tableList, Comparator.<String>naturalOrder().reversed());
	}

	private TableNodesComplete getTableNodesComplete() {
		// 获取当前数据源-并获取数据源类型
		DataSource ds = SpringUtil.getBean(DataSource.class);
		DbType dbType = MybatisUtil.getDbType(ds);
		Map<String, TableNodesComplete> beans = SpringUtil.getBeansOfType(TableNodesComplete.class);
		Assert.notNull(beans, "请注入一个生成表的实现类：{}", TableNodesComplete.class.getSimpleName());
		TableNodesComplete complete = CollUtil.findOne(beans.values(), b -> b.getDbType() == dbType);
		//
		Assert.notNull(complete, "未找到该数据库表生成插件：{}", dbType);
		return complete;
	}

	/**
	 * 构造日期范围查询参数
	 * 
	 * @param column    分片字段
	 * @param tableInfo 表信息
	 * @param args      参数列表
	 * @return 日期范围
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public DateRangeModel buildDateRange(String column, TableInfo tableInfo, Object[] args) {
		Class<?> entityType = tableInfo.getEntityType();
		// 1.解析entity
		Object data = getFirstParam(entityType, args);
		if (data == null) {
			// 2.解析map
			data = getFirstParam(Map.class, args);
		}
		if (data != null) {
			Date date = this.getPropertyValue(data, column, tableInfo);
			if (date != null) {
				return DateRangeModel.of()//
						.setKeyword(SqlKeyword.EQ)//
						.setDateList(CollUtil.toList(date));
			}
		}
		DateRangeModel model = null;
		// 3.解析条件包装类 AbstractWrapper
		AbstractWrapper wrapper = getFirstParam(AbstractWrapper.class, args);
		if (wrapper != null) {
			Map<String, Object> paramMap = wrapper.getParamNameValuePairs();
			model = WrapperUtil.parseDateRange(wrapper, column, paramMap);
		}
		if (model == null) {
			// 设置默认时间范围
			DateTime latest = DateUtil.date();
			model = DateRangeModel.of()//
					.setKeyword(SqlKeyword.BETWEEN)//
					.setUpperDate(latest);
		}
		if (!CollUtil.contains(WrapperUtil.exactSqlKeywords, model.getKeyword())) {
			Date end = model.getUpperDate();
			if (end == null) {
				end = DateUtil.date();
				model.setUpperDate(end);
			}
			if (model.getLowerDate() == null) {
				int offset = prop.getLatestMonths();
				// 向前推几个月
				DateTime start = DateUtil.offsetMonth(end, offset < 1 ? -1 : -offset);
				model.setLowerDate(start);
			}
		}
		return model;
	}

	/**
	 * 获取第一个参数类型
	 * 
	 * @param <T>  参数类型
	 * @param type type
	 * @param args 参数列表
	 * @return 参数值
	 */
	@SuppressWarnings("unchecked")
	public <T> T getFirstParam(Class<T> type, Object[] args) {
		boolean empty = ArrayUtil.isEmpty(args);
		if (empty) {
			return null;
		}
		return Stream.of(args).filter(arg -> type.isInstance(arg)).findFirst().map(arg -> (T) arg).orElse(null);

	}

	@SuppressWarnings("unchecked")
	public <T> T getPropertyValue(Object entity, String column, TableInfo tableInfo) {
		String property = WrapperUtil.getProperty(column, tableInfo);
		//
		Field field = ReflectUtil.getField(tableInfo.getEntityType(), property);
		Assert.notNull(field, "【{}】: 未匹配到分片字段", column);
		Class<?> type = field.getType();
		Assert.isAssignable(Date.class, type, "【字段类型错误】: Date ≠ {}", type);
		Object val = BeanUtil.getProperty(entity, property);
		if (val == null) {
			val = BeanUtil.getProperty(entity, column);
		}
		return (T) val;
	}

	/**
	 * 建议只传入一个排序字段；否则可能有偏差
	 * 
	 * @param list      数据列表
	 * @param orderList 排序列表
	 * @param tableInfo 表信息
	 * @return 排序后的数据
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<?> sort(List<?> list, List<String> orderList, TableInfo tableInfo) {
		if (CollUtil.isEmpty(orderList)) {
			return list;
		}
		Comparator comparator = null;
		for (String orderStr : orderList) {
			// 排序的列
			String column = StrUtil.subBefore(orderStr, StrUtil.SPACE, false);
			// 是否倒叙
			boolean desc = StrUtil.endWithIgnoreCase(StrUtil.SPACE + orderStr.trim(), SqlKeyword.DESC.name());
			String property = WrapperUtil.getProperty(column, tableInfo);
			if (comparator == null) {
				// 该字段必须实现Compareable，否则无法排序
				comparator = Comparator.comparing(e -> {
					return (Comparable) BeanUtil.getFieldValue(e, (e instanceof Map) ? column : property);
				});
			} else {
				// 上一个字段相同则按照下方字段排序
				comparator = comparator.thenComparing(e -> {
					return (Comparable) BeanUtil.getFieldValue(e, (e instanceof Map) ? column : property);
				});
			}
			if (desc) {
				comparator = comparator.reversed();
			}
		}
		// 排序之
		return CollUtil.sort(list, comparator);
	}

}
