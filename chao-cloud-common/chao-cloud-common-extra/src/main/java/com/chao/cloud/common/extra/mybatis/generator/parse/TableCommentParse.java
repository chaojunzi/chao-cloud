package com.chao.cloud.common.extra.mybatis.generator.parse;

import java.util.List;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.generator.config.po.TableField;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Data;

/**
 * 表注释解析
 * @author 薛超
 * @since 2019年8月1日
 * @version 1.0.5
 */
@Data
public class TableCommentParse {
	/**
	 * 表标题
	 */
	private String title;

	/**
	 * 模糊搜索
	 */
	private String like;

	public List<TableField> parseLike(List<TableField> source) {
		if (StrUtil.isNotBlank(like) && CollUtil.isNotEmpty(source)) {
			List<String> list = CollUtil.newArrayList(like.split(","));
			return source.stream().filter(f -> list.contains(f.getName())).collect(Collectors.toList());
		}
		return null;
	}

}
