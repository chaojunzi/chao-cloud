package com.chao.cloud.common.extra.mybatis.constant;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;

/**
 * mapper方法枚举
 * 
 * @author 薛超
 * @since 2022年5月17日
 * @version 1.0.0
 */
@RequiredArgsConstructor
public enum MapperEnum {
	/**
	 * 添加一条
	 */
	insert(MapperConstant.INSERT_METHOD), //
	/**
	 * 数量
	 */
	count(MapperConstant.COUNT_METHOD), //
	/**
	 * 一个
	 */
	selectOne(MapperConstant.SELECT_ONE_METHOD), //
	/**
	 * 列表
	 */
	list(MapperConstant.LIST_METHOD), //
	/**
	 * 分页
	 */
	page(MapperConstant.PAGE_METHOD), //
	/**
	 * 修改一条
	 */
	updateOne(MapperConstant.UPDATE_ONE_METHOD), //
	/**
	 * 修改多条
	 */
	updateMulti(MapperConstant.UPDATE_MULTI_METHOD), //
	;

	final String[] methodNameList;

	public static MapperEnum getMapperMethod(String methodName) {
		return ArrayUtil.firstMatch(m -> StrUtil.equalsAny(methodName, m.methodNameList), MapperEnum.values());
	}

}
