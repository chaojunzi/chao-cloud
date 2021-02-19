package com.chao.cloud.common.extra.mybatis.common;

import java.lang.reflect.Method;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;

import cn.hutool.core.util.ReflectUtil;

/**
 * 方法表达式
 * 
 * @author 薛超
 * @since 2021年1月25日
 * @version 1.0.0
 */
public interface FuncExps {
	/**
	 * 方法
	 */
	Method FUNC_METHOD = ReflectUtil.getMethod(FuncExps.class, "getSFunction");

	/**
	 * 获取方法表达式
	 * 
	 */
	<T, R> SFunction<T, R> getSFunction();

}
