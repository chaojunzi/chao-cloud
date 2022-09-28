package com.chao.cloud.common.extra.mybatis.constant;

/**
 * mapper的一些常量
 * 
 * @author 薛超
 * @since 2022年5月17日
 * @version 1.0.0
 */
public interface MapperConstant {
	/**
	 * 添加一条
	 */
	String[] INSERT_METHOD = { "insert" };
	/**
	 * 获取总数
	 */
	String[] COUNT_METHOD = { "selectCount" };
	/**
	 * 获取一个对象
	 */
	String[] SELECT_ONE_METHOD = { "selectOne", "selectById", "selectBatchIds" };
	/**
	 * 获取list
	 */
	String[] LIST_METHOD = { "selectList", "selectMaps", "selectObjs", "selectByMap" };
	/**
	 * 分页
	 */
	String[] PAGE_METHOD = { "selectMapsPage", "selectPage" };
	/**
	 * 修改一条
	 */
	String[] UPDATE_ONE_METHOD = { "updateById", "deleteById", "deleteBatchIds" };
	/**
	 * 修改多条
	 */
	String[] UPDATE_MULTI_METHOD = { "update", "delete", "deleteByMap" };

}
