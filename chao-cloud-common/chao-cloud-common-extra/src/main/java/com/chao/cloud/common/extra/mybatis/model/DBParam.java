package com.chao.cloud.common.extra.mybatis.model;

import javax.validation.constraints.NotBlank;

import lombok.Data;

/**
 * 数据库请求参数
 * 
 * @author 薛超
 * @since 2021年12月23日
 * @version 1.0.0
 */
@Data
public class DBParam {
	/**
	 * 数据库名
	 */
	@NotBlank(message = "数据库不能为空")
	private String dbName;

	// 或table
	@NotBlank(message = "请输入sql")
	private String sql;
	/**
	 * 当前页
	 */
	private int page = 1;
	/**
	 * 每页限制
	 */
	private int limit = 10;

}
