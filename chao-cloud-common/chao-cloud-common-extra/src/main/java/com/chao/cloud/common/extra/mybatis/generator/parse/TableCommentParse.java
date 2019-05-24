package com.chao.cloud.common.extra.mybatis.generator.parse;

import java.util.List;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.generator.config.po.TableField;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Data;

/**
 * 
 * @功能：表注释解析
 * @author： 薛超
 * @时间： 2019年5月24日
 * @version 1.0.0
 */
@Data
public class TableCommentParse {
	/**
	 *  目前只解析模糊搜索
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
