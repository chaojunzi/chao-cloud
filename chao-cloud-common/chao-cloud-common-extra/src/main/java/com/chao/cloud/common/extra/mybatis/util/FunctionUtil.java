package com.chao.cloud.common.extra.mybatis.util;

import java.io.File;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.ibatis.builder.MapperBuilderAssistant;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.LambdaUtils;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.core.toolkit.support.SerializedLambda;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.func.Func0;
import cn.hutool.core.lang.func.Func1;
import cn.hutool.core.lang.func.LambdaUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.StrUtil;

/**
 * Function工具类
 *
 * @author 薛超
 * @since 2020年12月8日
 * @version 1.0.0
 */
public interface FunctionUtil {

	MapperBuilderAssistant MAPPER_ASSISTANT = new MapperBuilderAssistant(new MybatisConfiguration(), StrUtil.EMPTY);

	static <R> R delTmpFile(File tmpFile, Function<File, R> function) {
		try {
			return function.apply(tmpFile);
		} finally {
			FileUtil.del(tmpFile);
		}
	}

	static <T, R> String getFieldName(SFunction<T, R> func) {
		SerializedLambda lambda = LambdaUtils.resolve(func);
		String methodName = lambda.getImplMethodName();
		return StrUtil.getGeneralField(methodName);
	}

	static <T, R> String getColumn(SFunction<T, R> func) {
		SerializedLambda lambda = LambdaUtils.resolve(func);
		String fieldName = StrUtil.getGeneralField(lambda.getImplMethodName());
		Class<?> beanClass = lambda.getImplClass();
		TableInfo info = buildTableInfo(beanClass);
		Assert.notNull(info, "无效的数据实体 beanClass={}", beanClass.getName());
		Map<String, String> propColumnMap = info.getFieldList().stream()
				.collect(Collectors.toMap(TableFieldInfo::getProperty, TableFieldInfo::getColumn));
		String column = propColumnMap.get(fieldName);
		Assert.notBlank(column, "无效的列名映射 fieldName={}", fieldName);
		return column;
	}

	static TableInfo buildTableInfo(Class<?> beanClass) {
		TableInfo info = TableInfoHelper.getTableInfo(beanClass);
		if (info != null) {
			return info;
		}
		return TableInfoHelper.initTableInfo(MAPPER_ASSISTANT, beanClass);
	}

	/**
	 * 构造wrapper之前添加缓存
	 * 
	 * @param <T>      实体类型
	 * @param beanType 实体类型
	 * @param func     构造方法
	 * @return wrapper 对象
	 */
	static <T, R extends Wrapper<T>> R aroundWrapper(Class<T> beanType, Func0<R> func) {
		buildTableInfo(beanType);
		return func.callWithRuntimeException();
	}

	/**
	 * 生成查询条件<必须声明参数类型> 例如：(TSalesCreate o) -> w
	 * 
	 * @param <T>  实体类类型
	 * @param <R>  条件
	 * @param func 实例化方法
	 * @return wrapper
	 */
	static <T, R extends Wrapper<T>> R wrapper(Func1<T, R> func) {
		// 获取实体类类型
		java.lang.invoke.SerializedLambda lambda = LambdaUtil.resolve(func);
		String methodType = lambda.getInstantiatedMethodType();
		String className = (methodType.substring(2, methodType.indexOf(';'))).replace('/', '.');
		Class<T> entityClass = ClassUtil.loadClass(className);
		buildTableInfo(entityClass);
		return func.callWithRuntimeException(null);
	}

	@SuppressWarnings("unchecked")
	static <T, R> Class<T> getBeanClass(SFunction<T, R> func) {
		SerializedLambda lambda = LambdaUtils.resolve(func);
		return (Class<T>) lambda.getImplClass();
	}

}
