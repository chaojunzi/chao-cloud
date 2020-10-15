package com.chao.cloud.common.extra.mybatis.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.chao.cloud.common.extra.mybatis.constant.MybatisConstant;

import cn.hutool.core.collection.CollUtil;

/**
 * mybatis 工具
 * 
 * @author 薛超
 * @since 2020年7月10日
 * @version 1.0.9
 */
public interface MybatisUtil {

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
