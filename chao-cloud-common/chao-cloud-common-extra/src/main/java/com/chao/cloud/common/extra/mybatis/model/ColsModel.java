package com.chao.cloud.common.extra.mybatis.model;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 列对象
 * 
 * @author 薛超
 * @since 2021年12月23日
 * @version 1.0.0
 */
@Data
@Accessors(chain = true)
public class ColsModel {
	/**
	 * 属性
	 */
	private String field;
	/**
	 * 标题
	 */
	private String title;
	/**
	 * 固定位置：right/left
	 */
	private String fixed;
	/**
	 * 类型
	 */
	private String type;
}
