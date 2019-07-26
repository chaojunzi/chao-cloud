package com.chao.cloud.common.util;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.chao.cloud.common.constants.TreeEnum;
import com.chao.cloud.common.entity.TreeEntity;
import com.chao.cloud.common.exception.BusinessException;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;

/**
 * 实体转换
 * @功能：
 * @author： 薛超
 * @时间：2019年7月26日
 * @version 1.0.3
 */
public class EntityUtil {

	/**
	 * 转换list
	 * @param source 要转换的list
	 * @param clazz 需要转换的 class
	 *            
	 */
	public static <T, S> List<T> listConver(List<S> source, Class<T> clazz) {
		if (ObjectUtil.isNull(clazz)) {
			throw new BusinessException("class is null");
		}
		if (!CollUtil.isEmpty(source)) {
			return source.stream().map(s -> BeanUtil.toBean(s, clazz)).collect(Collectors.toList());
		}
		return Collections.emptyList();
	}

	/**
	 * 1.解析树形数据-实现接口型
	 * 递归算法
	 * 实体类需要实现 {@link TreeEntity}
	 * @param entityList（数据源）
	 * @param topId 顶级id（八大基本类型或String）
	 * @return
	 */
	public static <E extends TreeEntity<E>> List<E> toTreeList(List<E> entityList, Serializable topId) {
		if (CollUtil.isEmpty(entityList)) {
			return Collections.emptyList();
		}
		// 获取顶层元素集合
		List<E> resultList = entityList.stream().filter(e -> e.getParentId() == null || e.getParentId().equals(topId))
				.collect(Collectors.toList());
		// 递归获取每个顶层元素的子数据集合
		resultList.forEach(r -> recursiveFill(entityList, r));
		return resultList;
	}

	/**
	 * 1.解析树形数据-注解型
	 * 递归算法
	 * 实体类需要增加3个注解 {@link @TreeAnnotation(TreeEnum.ID)}
	 * @param entityList（数据源）
	 * @param topId 顶级id（八大基本类型或String）
	 * @return
	 */
	public static <T> List<T> toTreeAnnoList(List<T> entityList, Serializable topId) {
		if (CollUtil.isEmpty(entityList)) {
			return Collections.emptyList();
		}
		// 校验实体类是否具备解析条件
		Map<TreeEnum, Field> treeMap = TreeEnum.convertTreeMap(entityList.get(0).getClass());
		// 获取顶层元素集合
		List<T> resultList = entityList.stream()
				.filter(e -> ReflectUtil.getFieldValue(e, treeMap.get(TreeEnum.PARENT_ID)) == null
						|| ReflectUtil.getFieldValue(e, treeMap.get(TreeEnum.PARENT_ID)).equals(topId))
				.collect(Collectors.toList());
		// 数据整合
		// 递归获取每个顶层元素的子数据集合
		resultList.forEach(r -> recursiveFill(entityList, r, treeMap));
		return resultList;
	}

	/**
	 * 递归填充子集
	 * @param entityList（数据源）
	 * @param root 根对象
	 */
	public static <E extends TreeEntity<E>> E recursiveFill(List<E> entityList, E root) {
		Object parentId = root.getId();
		Assert.notNull(parentId, "[Id 异常：{}]", root);
		// 循环并获取子集对象
		List<E> subList = entityList.stream().filter(e -> parentId.equals(e.getParentId()))
				.collect(Collectors.toList());
		// 递归子集
		subList.forEach(s -> recursiveFill(entityList, s));
		// 返回
		root.setSubList(subList);
		return root;
	}

	/**
	 * 递归填充子集
	 * @param entityList（数据源）
	 * @param root 根对象
	 * @param treeMap 字段属性的必要条件
	 */
	public static void recursiveFill(List<?> entityList, Object root, Map<TreeEnum, Field> treeMap) {
		Object parentId = ReflectUtil.getFieldValue(root, treeMap.get(TreeEnum.ID));
		Assert.notNull(parentId, "[Id 异常：{}]", root);
		// 循环并获取子集对象
		List<?> subList = entityList.stream()
				.filter(e -> parentId.equals(ReflectUtil.getFieldValue(e, treeMap.get(TreeEnum.PARENT_ID))))
				.collect(Collectors.toList());
		// 递归子集
		subList.forEach(s -> recursiveFill(entityList, s, treeMap));
		// 返回
		ReflectUtil.setFieldValue(root, treeMap.get(TreeEnum.SUB_LIST), subList);
	}

}