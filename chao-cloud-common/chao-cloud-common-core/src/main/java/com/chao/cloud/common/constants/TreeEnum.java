package com.chao.cloud.common.constants;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.chao.cloud.common.annotation.TreeAnnotation;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ReflectUtil;

/**
 * 
 * @功能：树形结构
 * @author： 薛超
 * @时间： 2019年7月26日
 * @version 1.0.0
 */
public enum TreeEnum {
	/**
	 * ID-> 唯一标识
	 */
	ID,
	/**
	 * 父ID
	 */
	PARENT_ID,
	/**
	 * 子集
	 */
	SUB_LIST;
	/**
	 * 转换map
	 * @param beanType
	 * @return
	 */
	public static Map<TreeEnum, Field> convertTreeMap(Class<?> beanType) {
		// 获取属性
		List<Field> list = CollUtil.toList(ReflectUtil.getFields(beanType));
		Assert.notNull(list, "[Class:{} not has Field]", beanType);
		// 获取注解
		Map<TreeAnnotation, List<Field>> treeMap = list.stream()
				.filter(f -> f.isAnnotationPresent(TreeAnnotation.class))
				.collect(Collectors.groupingBy(f -> f.getAnnotation(TreeAnnotation.class)));
		// 断言数量是否超出
		treeMap.forEach((k, v) -> {
			Assert.isTrue(v.size() == 1, "[@TreeAnnotation(TreeEnum.{})数量超过 1]", k.value());
		});
		// 断言数量是否符合
		Assert.isTrue(TreeEnum.values().length == treeMap.size(), "[请确认:{} 是否全部包含 TreeAnnotation({})]", beanType,
				TreeEnum.values());
		// 返回
		return treeMap.keySet().stream().collect(Collectors.toMap(TreeAnnotation::value, k -> treeMap.get(k).get(0)));
	}

}
