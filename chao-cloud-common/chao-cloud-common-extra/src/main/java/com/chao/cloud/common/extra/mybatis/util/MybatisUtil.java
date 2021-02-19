package com.chao.cloud.common.extra.mybatis.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.conditions.AbstractWrapper;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.chao.cloud.common.extra.mybatis.annotation.QueryCondition;
import com.chao.cloud.common.extra.mybatis.common.FuncExps;
import com.chao.cloud.common.extra.mybatis.common.SqlTemplate;
import com.chao.cloud.common.extra.mybatis.constant.MybatisConstant;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;

/**
 * mybatis 工具
 * 
 * @author 薛超
 * @since 2020年7月10日
 * @version 1.0.9
 */
public interface MybatisUtil {

	Map<Class<? extends FuncExps>, SFunction<?, ?>> FUNC_MAP = new ConcurrentHashMap<>();

	/**
	 * 解析 {@link QueryCondition}
	 * 
	 * @param <T>       数据实体
	 * @param param     参数
	 * @param beanClass 实体类型
	 * @param wrapper   条件构造器
	 * @return {@link AbstractWrapper}
	 */
	public static <T> AbstractWrapper<T, ?, ?> parseQueryCondition(Object param, Class<T> beanClass,
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
			if (!sqlSegment) {
				Assert.notBlank(column, "数据库字段不匹配;paramName={}", field.getName());
			}
			// 获取属性值
			Object v = ReflectUtil.getFieldValue(param, field);
			if (v == null) {
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
				wrapper.apply(v.toString());
			} else {
				wrapper.apply(template.getApplySql(column), template.formatValue(v));
			}
		}
		return wrapper;
	}

	public static SFunction<?, ?> getSFunction(Class<? extends FuncExps> funcClass) {
		if (FUNC_MAP.containsKey(funcClass)) {
			return FUNC_MAP.get(funcClass);
		}
		SFunction<?, ?> func = ReflectUtil.invoke(ReflectUtil.newInstance(funcClass), FuncExps.FUNC_METHOD);
		FUNC_MAP.put(funcClass, func);
		return func;
	}

	public static <T> AbstractWrapper<T, ?, ?> parseQueryCondition(Object param, Class<T> beanClass) {
		return parseQueryCondition(param, beanClass, true);
	}

	public static <T> AbstractWrapper<T, ?, ?> parseQueryCondition(Object param, Class<T> beanClass, boolean isLambda) {
		return parseQueryCondition(param, beanClass, isLambda ? Wrappers.<T>lambdaQuery() : Wrappers.<T>query());
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
	public static <T> T getOne(IService<T> service, AbstractWrapper<T, ?, ?> wrapper) {
		// 判断方言
		String dbType = SpringUtil.getProperty(Constants.MYBATIS_PLUS + ".db-type");
		DbType type = DbType.getDbType(dbType);
		switch (type) {
		case ORACLE:
			wrapper.apply(MybatisConstant.LIMIT_1_ORACLE);
			break;
		case MYSQL:
		default:// 默认mysql
			wrapper.last(MybatisConstant.LIMIT_1);
			break;
		}
		return service.getOne(wrapper);
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
}
