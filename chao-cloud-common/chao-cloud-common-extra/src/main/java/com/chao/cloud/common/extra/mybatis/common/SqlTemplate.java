package com.chao.cloud.common.extra.mybatis.common;

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * SQL 模板
 * 
 * @author 薛超
 * @since 2021年1月12日
 * @version 1.0.0
 */
@AllArgsConstructor
@Getter
public enum SqlTemplate {

	EQ("{} = {0}", StrUtil.EMPTY), //
	LIKE("{} LIKE {0}", "%{}%"), //
	LIKE_RIGHT("{} LIKE {0}", "{}%"), //
	LIKE_LEFT("{} LIKE {0}", "%{}"), //
	NE("{} <> {0}", StrUtil.EMPTY), //
	GT("{} > {0}", StrUtil.EMPTY), //
	GE("{} >= {0}", StrUtil.EMPTY), //
	LT("{} < {0}", StrUtil.EMPTY), //
	LE("{} <= {0}", StrUtil.EMPTY), //
	/**
	 * 排序-> 请使用boolean类型 <br>
	 * true->asc <br>
	 * false->desc <br>
	 */
	ORDER_BY("orderBy", StrUtil.EMPTY), //
	IN("in", StrUtil.EMPTY), //
	SEGMENT("{}", StrUtil.EMPTY);// sql 片段-对应的属性必须是String类型

	private final String template;
	private final String valueTemplate;

	public String getApplySql(String column) {
		return StrUtil.format(template, column);
	}

	public Object formatValue(Object value) {
		if (StrUtil.EMPTY.equals(valueTemplate)) {
			return value;
		}
		return StrUtil.format(valueTemplate, value);
	}

}
