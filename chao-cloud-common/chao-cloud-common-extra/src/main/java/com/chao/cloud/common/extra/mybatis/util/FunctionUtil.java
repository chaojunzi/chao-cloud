package com.chao.cloud.common.extra.mybatis.util;

import java.io.File;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.LambdaUtils;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.core.toolkit.support.SerializedLambda;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;

/**
 * Function工具类
 *
 * @author 薛超
 * @since 2020年12月8日
 * @version 1.0.0
 */
public interface FunctionUtil {

	static <R> R delTmpFile(File tmpFile, Function<File, R> function) {
		try {
			return function.apply(tmpFile);
		} finally {
			FileUtil.del(tmpFile);
		}
	}

	/**
	 * 根据方法表达式获取属性
	 * 
	 * @param <T>  参数类型
	 * @param <R>  返回值类型
	 * @param func 方法表达式
	 * @return 属性名称
	 */
	static <T, R> String getFieldName(SFunction<T, R> func) {
		SerializedLambda lambda = LambdaUtils.resolve(func);
		String methodName = lambda.getImplMethodName();
		return StrUtil.getGeneralField(methodName);
	}

	/**
	 * 根据方法表达式获取column
	 * 
	 * @param <T>  参数类型
	 * @param <R>  返回值类型
	 * @param func 方法表达式
	 * @return 列
	 */
	static <T, R> String getColumn(SFunction<T, R> func) {
		SerializedLambda lambda = LambdaUtils.resolve(func);
		String fieldName = StrUtil.getGeneralField(lambda.getImplMethodName());
		Class<?> beanClass = lambda.getImplClass();
		TableInfo info = TableInfoHelper.getTableInfo(beanClass);
		Assert.notNull(info, "无效的数据实体 beanClass={}", beanClass.getName());
		Map<String, String> propColumnMap = info.getFieldList().stream()
				.collect(Collectors.toMap(TableFieldInfo::getProperty, TableFieldInfo::getColumn));
		String column = propColumnMap.get(fieldName);
		Assert.notBlank(column, "无效的列名映射 fieldName={}", fieldName);
		return column;
	}

}
