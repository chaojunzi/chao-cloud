package com.chao.cloud.common.extra.mybatis.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 数据库模型
 * 
 * @author 薛超
 * @since 2021年12月22日
 * @version 1.0.0
 */
@Data
@Accessors(chain = true)
public class DBModel {
	/**
	 * table与字段对应关系
	 */
	private Map<String, List<String>> tableWords = new LinkedHashMap<>();
	/**
	 * 
	 */
	private List<TableModel> tableList = new ArrayList<>(0);

	/**
	 * table详情（共三级）<br>
	 * 1.数据库<br>
	 * 2.表<br>
	 * 3.列<br>
	 */
	@Data
	@Accessors(chain = true)
	public static class TableModel {
		private String title;// 名称
		private String dbName;// 数据库名称
		private String icon;// 图标
		private boolean spread;// 是否展开
		private boolean disabled;// 固定不变的-不可编辑
		private boolean checked;//
		private List<TableModel> children;// 若为table|或为列
	}

}