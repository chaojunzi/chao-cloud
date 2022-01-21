package com.chao.cloud.common.extra.mybatis.model;

import java.util.List;

import cn.hutool.db.Entity;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 数据库返回值
 * 
 * @author 薛超
 * @since 2021年12月23日
 * @version 1.0.0
 */
@Data
@Accessors(chain = true)
@RequiredArgsConstructor
public class DBEntity {
	/**
	 * 是否返回了resultSet
	 */
	private final boolean hasResult;
	/**
	 * sql语句
	 */
	private final String sql;
	/**
	 * 记录
	 */
	private List<Entity> records;
	/**
	 * 列
	 */
	private List<ColsModel> colsList;
	/**
	 * 处理的记录数
	 */
	private int total;
	/**
	 * 执行所经过的时间
	 */
	private String time;
	/**
	 * 执行信息
	 */
	private String msg = "OK";
}
