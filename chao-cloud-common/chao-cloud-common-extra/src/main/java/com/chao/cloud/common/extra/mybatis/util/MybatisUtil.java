package com.chao.cloud.common.extra.mybatis.util;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.sql.DataSource;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.conditions.AbstractWrapper;
import com.baomidou.mybatisplus.core.conditions.ISqlSegment;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.segments.AbstractISegmentList;
import com.baomidou.mybatisplus.core.conditions.segments.MergeSegments;
import com.baomidou.mybatisplus.core.enums.SqlKeyword;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.core.toolkit.support.SerializedLambda;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.toolkit.JdbcUtils;
import com.chao.cloud.common.extra.mybatis.annotation.QueryCondition;
import com.chao.cloud.common.extra.mybatis.common.ApplySql;
import com.chao.cloud.common.extra.mybatis.common.ApplySql.ApplySqlSegment;
import com.chao.cloud.common.extra.mybatis.common.ApplySql.ParamValue;
import com.chao.cloud.common.extra.mybatis.common.FuncExps;
import com.chao.cloud.common.extra.mybatis.common.SqlTemplate;
import com.chao.cloud.common.extra.mybatis.constant.MybatisConstant;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.DbUtil;
import cn.hutool.extra.spring.SpringUtil;

/**
 * mybatis 工具
 * 
 * @author 薛超
 * @since 2020年7月10日
 * @version 1.0.9
 */
public interface MybatisUtil {

	/**
	 * sql 模板
	 */
	String SELECT_SQL_TEMPLATE = "SELECT {} FROM {} {}";

	Map<Class<? extends FuncExps>, WeakReference<SFunction<?, ?>>> FUNC_MAP = new ConcurrentHashMap<>();

	/**
	 * 解析 {@link QueryCondition}
	 * 
	 * @param <T>       数据实体
	 * @param param     参数
	 * @param beanClass 实体类型
	 * @param wrapper   条件构造器
	 * @return {@link AbstractWrapper}
	 */
	static <T> AbstractWrapper<T, ?, ?> parseQueryCondition(Object param, Class<T> beanClass,
			AbstractWrapper<T, ?, ?> wrapper) {
		if (param == null) {
			return wrapper;
		}
		Field[] fields = ReflectUtil.getFields(param.getClass());
		if (ArrayUtil.isEmpty(fields)) {
			return wrapper;
		}
		// 获取字段名
		TableInfo info = TableInfoHelper.getTableInfo(beanClass);
		Assert.notNull(info, "无效的数据实体 beanClass={}", beanClass.getName());
		Map<String, String> propColumnMap = info.getFieldList().stream()
				.collect(Collectors.toMap(TableFieldInfo::getProperty, TableFieldInfo::getColumn));
		for (Field field : fields) {
			// 判断model中是否存在 @QueryCondition 注解
			QueryCondition q = field.getAnnotation(QueryCondition.class);
			if (q == null) {
				continue;
			}
			SqlTemplate template = q.value();
			// 实体字段名字
			Class<? extends FuncExps> funcClass = q.funcClass();
			String fieldName = field.getName();
			SFunction<?, ?> func = null;
			if (funcClass != FuncExps.class) {
				// 调用sfunction
				func = getSFunction(funcClass);
				String name = FunctionUtil.getFieldName(func);
				if (StrUtil.isNotBlank(name)) {
					fieldName = name;
				}
			}
			//
			String column = propColumnMap.get(fieldName);
			//
			boolean sqlSegment = template == SqlTemplate.SEGMENT;
			boolean isSql = template == SqlTemplate.APPLY_SQL;
			if (!sqlSegment && !isSql) {
				Assert.notBlank(column, "数据库字段不匹配;paramName={}", field.getName());
			}
			// 获取属性值
			Object v = ReflectUtil.getFieldValue(param, field);
			if (v == null) {
				continue;
			}
			// 处理自定义sql
			if (isSql) {
				parseApplySql(wrapper, v);
				continue;
			}
			// 排序单独处理
			if (template == SqlTemplate.ORDER_BY && func != null) {
				Assert.state(v instanceof Boolean, "排序请使用 Boolean 类型（B大写）");
				// 反射方法
				Method method = ReflectUtil.getMethodByName(wrapper.getClass(), template.getTemplate());
				ReflectUtil.invoke(wrapper, method, true, v, func);
				continue;
			}
			// 处理in查询-占位符
			if (template == SqlTemplate.IN) {
				Assert.state(v instanceof Collection, "in 查询 请传入 集合");
				// 反射方法
				Method method = ReflectUtil.getMethodByName(wrapper.getClass(), template.getTemplate());
				ReflectUtil.invoke(wrapper, method, true, func, v);
				continue;
			}
			//
			if (sqlSegment) {// 必须是String类型
				if (v instanceof List) {
					List<String> strList = (List<String>) v;
					strList.forEach(wrapper::apply);
				}
				if (v instanceof String) {
					wrapper.apply(v.toString());
				}
			} else {
				wrapper.apply(template.getApplySql(column), template.formatValue(v));
			}
		}
		return wrapper;
	}

	/**
	 * 解析{@link ApplySql}
	 * 
	 * @param <T>     实体类类型
	 * @param wrapper 构造条件
	 * @param v       ApplySql->实体类型
	 */
	static <T> void parseApplySql(AbstractWrapper<T, ?, ?> wrapper, Object v) {
		if (v instanceof ApplySql) {
			ApplySql as = (ApplySql) v;
			ApplySqlSegment normalSegment = as.getNormalSegment();
			List<String> groupByList = as.getGroupByList();
			List<String> orderByList = as.getOrderByList();
			ApplySqlSegment havingSegment = as.getHavingSegment();
			// 自定义sql
			if (normalSegment != null) {
				wrapper.apply(normalSegment.getSqlSegment(), getValueArray(normalSegment.getValueList()));
			}
			Method doItMethod = ReflectUtil.getMethodByName(wrapper.getClass(), SqlTemplate.DO_IT.getTemplate());
			// group by
			if (CollUtil.isNotEmpty(groupByList)) {
				ISqlSegment seg = () -> CollUtil.join(groupByList, StrUtil.COMMA);
				ReflectUtil.invoke(wrapper, doItMethod, true, new ISqlSegment[] { SqlKeyword.GROUP_BY, seg });
			}
			// having
			if (havingSegment != null) {
				wrapper.having(havingSegment.getSqlSegment(), getValueArray(havingSegment.getValueList()));
			}
			// order by
			if (CollUtil.isNotEmpty(orderByList)) {
				ISqlSegment seg = () -> CollUtil.join(orderByList, StrUtil.COMMA);
				ReflectUtil.invoke(wrapper, doItMethod, true, new ISqlSegment[] { SqlKeyword.ORDER_BY, seg });
			}
		}
	}

	static Object[] getValueArray(List<ParamValue> list) {
		Object[] valueArray = null;
		if (CollUtil.isNotEmpty(list)) {
			valueArray = list.stream().map(l -> {
				if (StrUtil.isBlank(l.getType())) {
					return null;
				}
				return Convert.convert(ClassUtil.loadClass(l.getType()), l.getValue());
			}).toArray();
		}
		return valueArray;
	}

	/**
	 * 构造applysql
	 * 
	 * @param <T>       实体类类型
	 * @param wrapper   构造条件
	 * @param beanClass 实体类类型
	 * @return {@link ApplySql}
	 */
	static <T> ApplySql buildApplySql(LambdaQueryWrapper<T> wrapper, Class<T> beanClass) {
		// sql片段
		ApplySqlSegment normalSegment = null;
		List<String> groupByList = Collections.emptyList();
		ApplySqlSegment havingSegment = null;
		List<String> orderByList = Collections.emptyList();
		// 判断bean是否加载
		TableInfo info = FunctionUtil.buildTableInfo(beanClass);
		Assert.notNull(info, "无效的数据实体 beanClass={}", beanClass.getName());
		// 获取groupBy和orderBy
		MergeSegments segments = wrapper.getExpression();
		if (segments == null) {
			return null;
		}
		// 1.where之后的条件
		normalSegment = buildApplySqlSegment(segments.getNormal(), wrapper.getParamNameValuePairs());
		// 2.group by
		groupByList = CollUtil.map(segments.getGroupBy(), ISqlSegment::getSqlSegment, true);
		// 3.having 之后的条件
		havingSegment = buildApplySqlSegment(segments.getHaving(), wrapper.getParamNameValuePairs());
		// 4.order by
		orderByList = CollUtil.map(segments.getOrderBy(), ISqlSegment::getSqlSegment, true);
		return ApplySql.of()//
				.setNormalSegment(normalSegment)//
				.setGroupByList(groupByList)//
				.setHavingSegment(havingSegment)//
				.setOrderByList(orderByList);
	}

	/**
	 * 构建sql及条件
	 * 
	 * @param segmentList sql片段
	 * @param valuePairs  参数值
	 * @return {@link ApplySqlSegment}
	 */
	static ApplySqlSegment buildApplySqlSegment(AbstractISegmentList segmentList, Map<String, Object> valuePairs) {
		// 获取sql片段
		String sqlSegment = segmentList.stream().map(ISqlSegment::getSqlSegment)
				.collect(Collectors.joining(StrUtil.SPACE));
		if (StrUtil.isBlank(sqlSegment)) {
			return null;
		}
		List<ParamValue> valueList = null;
		// 获取占位符中的key
		List<String> keyList = ReUtil.findAllGroup1("#\\{ew.paramNameValuePairs.(.*?)\\}", sqlSegment);
		// 获取参数值
		if (CollUtil.isNotEmpty(keyList)) {
			valueList = CollUtil.map(keyList, k -> {
				Object v = valuePairs.get(k);
				String type = null;
				Class<Object> paramType = ClassUtil.getClass(v);
				if (paramType != null) {
					type = paramType.getName();
					if (ClassUtil.isAssignable(Date.class, paramType)) {
						// 防止传入其他的Date.class
						type = Date.class.getName();
					}
				}
				return ParamValue.of().setType(type).setValue(v);
			}, true);
		}
		// sql 处理
		sqlSegment = replacePlaceholder(
				sqlSegment.replaceAll("#\\{ew.paramNameValuePairs.MPGENVAL[0-9]+\\}", MybatisConstant.ASK));
		return ApplySqlSegment.of().setSqlSegment(sqlSegment).setValueList(valueList);
	}

	public static String replacePlaceholder(String targetSql) {
		Assert.notBlank(targetSql, "sql 不能为空");
		StringBuilder sb = new StringBuilder("");
		int count = 0;
		for (int i = 0; i < targetSql.length(); i++) {
			if (StrUtil.equals(MybatisConstant.ASK, StrUtil.toString(targetSql.charAt(i)), true)) {
				sb.append("{" + count++ + "}");
			} else {
				sb.append(targetSql.charAt(i));
			}
		}
		return sb.toString();
	}

	/**
	 * 根据wrapper生成sql 如：a=1 and b=2
	 * 
	 * @param <T>       实体类型
	 * @param wrapper   条件
	 * @param beanClass bean类型
	 * @return sql条件
	 */
	static <T> String buildSql(LambdaQueryWrapper<T> wrapper, Class<T> beanClass) {
		// 判断bean是否加载
		TableInfo info = FunctionUtil.buildTableInfo(beanClass);
		Assert.notNull(info, "无效的数据实体 beanClass={}", beanClass.getName());
		String targetSql = wrapper.getTargetSql();
		if (StrUtil.isBlank(targetSql)) {
			return null;
		}
		int count = StrUtil.count(targetSql, MybatisConstant.ASK);
		Map<String, Object> valuePairs = wrapper.getParamNameValuePairs();
		Object[] vals = IntStream.range(1, count + 1)//
				.mapToObj(i -> {
					Object obj = valuePairs.get("MPGENVAL" + i);
					if (obj instanceof Date) {
						obj = DateUtil.formatDateTime((Date) obj);
					}
					return obj;
				}).toArray();
		// 去掉首尾括号
		return StrUtil.format(StrUtil.replace(targetSql, MybatisConstant.ASK, "'{}'"), vals);
	}

	/**
	 * 构造完全的sql 如： select * from table where a=1
	 * 
	 * @param <T>       实体类型
	 * @param wrapper   条件
	 * @param beanClass bean类型
	 * @return sql语句
	 */
	static <T> String buildCompleteSql(LambdaQueryWrapper<T> wrapper, Class<T> beanClass) {
		String where = buildSql(wrapper, beanClass);
		// where 条件
		where = StrUtil.isBlank(where) ? StrUtil.EMPTY : "WHERE " + where;
		// 表名
		TableInfo info = FunctionUtil.buildTableInfo(beanClass);
		// 字段
		String sqlSelect = StrUtil.isBlank(wrapper.getSqlSelect()) ? info.getAllSqlSelect() : wrapper.getSqlSelect();
		return StrUtil.format(SELECT_SQL_TEMPLATE, sqlSelect, info.getTableName(), where);
	}

	static SFunction<?, ?> getSFunction(Class<? extends FuncExps> funcClass) {
		SFunction<?, ?> func = Optional.ofNullable(FUNC_MAP.get(funcClass))//
				.map(WeakReference::get)//
				.orElse(null);
		if (func == null) {
			func = ReflectUtil.invoke(ReflectUtil.newInstance(funcClass), FuncExps.FUNC_METHOD);
			FUNC_MAP.put(funcClass, new WeakReference<>(func));
		}
		return func;
	}

	static <T> AbstractWrapper<T, ?, ?> parseQueryCondition(Object param, Class<T> beanClass) {
		return parseQueryCondition(param, beanClass, true);
	}

	static <T> AbstractWrapper<T, ?, ?> parseQueryCondition(Object param, Class<T> beanClass, boolean isLambda) {
		return parseQueryCondition(param, beanClass, isLambda ? Wrappers.<T>lambdaQuery() : Wrappers.<T>query());
	}

	/**
	 * 根据实体获取列名
	 * 
	 * @param func 表达式
	 * @return column
	 */
	static <T, R> String getColumn(SFunction<T, R> func) {
		SerializedLambda lambda = SerializedLambda.resolve(func);
		// 获取实体类型
		Class<?> entityClass = lambda.getImplClass();
		// 获取方法类型
		String fieldName = StrUtil.getGeneralField(lambda.getImplMethodName());
		// 获取属性
		Field field = ReflectUtil.getField(entityClass, fieldName);
		TableField tableField = AnnotationUtil.getAnnotation(field, TableField.class);
		if (tableField != null) {
			String column = tableField.value();
			if (StrUtil.isNotBlank(column)) {
				return column;
			}
		}
		return StrUtil.toUnderlineCase(fieldName);
	}

	/**
	 * 获取一条数据；<br>
	 * 注：<br>
	 * 1.请注入 @EnableMybatisPlusConfig <br>
	 * 2.目前只支持一种数据源类型 <br>
	 * 3.oracle|mysql
	 * 
	 * @param <T>     数据实体
	 * @param service 实体对应的service层
	 * @param wrapper 封装条件
	 * @return 实体
	 */
	static <T> T getOne(IService<T> service, AbstractWrapper<T, ?, ?> wrapper) {
		// 判断方言
		String dbType = SpringUtil.getProperty(Constants.MYBATIS_PLUS + ".db-type");
		DbType type = DbType.getDbType(dbType);
		switch (type) {
		case ORACLE:
			wrapper.apply(MybatisConstant.LIMIT_1_ORACLE);
			break;
		case MYSQL:
		default:// 默认mysql
			wrapper.last(MybatisConstant.LIMIT_1_MYSQL);
			break;
		}
		return service.getOne(wrapper);
	}

	/**
	 * 分页重构
	 * 
	 * @param <T>  实体类型
	 * @param page 分页
	 * @return 分页
	 */
	static <T> IPage<T> rebuild(IPage<T> page) {
		if (page.getSize() < 0) {
			page.setTotal(CollUtil.size(page.getRecords()));
		}
		return page;
	}

	/**
	 * 分页递归
	 * 
	 * @param <T>     数据实体
	 * @param current 当前页
	 * @param service 实体对应的service层
	 * @param wrapper 封装条件
	 * @param action  单个对象处理逻辑
	 */
	public static <T> void forEachPageRecursion(int current, IService<T> service, Wrapper<T> wrapper,
			Consumer<T> action) {
		forEachPageRecursion(current, MybatisConstant.SIZE, service, wrapper, action);
	}

	/**
	 * 分页递归
	 * 
	 * @param <T>     数据实体
	 * @param current 当前页
	 * @param size    每页的数量
	 * @param service 实体对应的service层
	 * @param wrapper 封装条件
	 * @param action  单个对象处理逻辑
	 */
	public static <T> void forEachPageRecursion(long current, long size, IService<T> service, Wrapper<T> wrapper,
			Consumer<T> action) {
		// 分页对象
		Page<T> page = new Page<>(current, size);
		// 查询
		service.page(page, wrapper);
		// 获取数据并处理
		List<T> list = page.getRecords();
		// 执行action
		list.forEach(action);
		// 判断是否有下一页
		if (page.hasNext()) {// 查询下一页并处理
			forEachPageRecursion(current + 1, size, service, wrapper, action);
		}
	}

	/**
	 * list分页递归
	 * 
	 * @param <T>     数据实体
	 * @param current 当前页
	 * @param service 实体对应的service层
	 * @param wrapper 封装条件
	 * @param result  返回的list
	 * @return result
	 */
	public static <T> List<T> listPageRecursion(long current, IService<T> service, Wrapper<T> wrapper, List<T> result) {
		return listPageRecursion(current, MybatisConstant.SIZE, service, wrapper, null);
	}

	/**
	 * list分页递归
	 * 
	 * @param <T>     数据实体
	 * @param current 当前页
	 * @param size    每页的数量
	 * @param service 实体对应的service层
	 * @param wrapper 封装条件
	 * @param result  返回的list
	 * @return result
	 */
	public static <T> List<T> listPageRecursion(long current, long size, IService<T> service, Wrapper<T> wrapper,
			List<T> result) {
		if (result == null) {
			result = new ArrayList<T>();
		}
		// 分页对象
		Page<T> page = new Page<>(current, size);
		// 查询
		service.page(page, wrapper);
		// 获取数据并处理
		List<T> list = page.getRecords();
		if (CollUtil.isNotEmpty(list)) {
			result.addAll(list);
		}
		// 判断是否有下一页
		if (page.hasNext()) {// 查询下一页并处理
			listPageRecursion(current + 1, size, service, wrapper, result);
		}
		return result;
	}

	static DatabaseMetaData getMetaData(DataSource ds) {
		Connection conn = null;
		try {
			conn = ds.getConnection();
			return conn.getMetaData();
		} catch (Exception e) {
			throw ExceptionUtil.wrapRuntime(e);
		} finally {
			DbUtil.close(conn);
		}
	}

	static DbType getDbType(DataSource ds) {
		try {
			DatabaseMetaData metaData = getMetaData(ds);
			return JdbcUtils.getDbType(metaData.getURL());
		} catch (Exception e) {
			throw ExceptionUtil.wrapRuntime(e);
		}

	}
}
