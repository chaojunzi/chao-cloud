package com.chao.cloud.common.util;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.chao.cloud.common.annotation.TreeAnnotation;
import com.chao.cloud.common.constant.TreeEnum;
import com.chao.cloud.common.entity.TreeEntity;
import com.chao.cloud.common.exception.BusinessException;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;

/**
 * 实体转换
 * 
 * @author 薛超
 * @since 2019年8月28日
 * @version 1.0.7
 */
public final class EntityUtil {
	/**
	 * 空值转换
	 * 
	 * @param <T>  对象泛型
	 * @param bean 对象实体
	 * @return 对象
	 */
	public static <T> T nullToEmpty(T bean) {
		if (bean != null && BeanUtil.isBean(bean.getClass())) {
			Field[] fields = ReflectUtil.getFields(bean.getClass());
			for (Field field : fields) {
				Class<?> type = field.getType();
				Object value = ReflectUtil.getFieldValue(bean, field);
				// 字符串处理
				if (type == String.class && value == null) {
					ReflectUtil.setFieldValue(bean, field, StrUtil.EMPTY);
					continue;
				}
				// 简单类型不进行处理
				if (value == null || ClassUtil.isSimpleValueType(type)) {
					continue;
				}
				// list
				if (value instanceof List) {
					nullToEmptyList((List) value);
					continue;
				}
				// 数组
				if (type.isArray()) {
					nullToEmptyArray((Object[]) value);
					continue;
				}
				// bean
				if (BeanUtil.isBean(type)) {
					nullToEmpty(value);
					continue;
				}
			}
		}
		return bean;
	}

	/**
	 * 空值转换
	 * 
	 * @param <T>  对象泛型
	 * @param list 对象集合
	 * @return 集合
	 */
	public static <T> List<T> nullToEmptyList(List<T> list) {
		if (CollUtil.isNotEmpty(list)) {
			for (Object bean : list) {
				nullToEmpty(bean);
			}
		}
		return list;
	}

	/**
	 * 空值转换
	 * 
	 * @param <T>   对象泛型
	 * @param array 对象数组
	 * @return 数组
	 */
	public static <T> T[] nullToEmptyArray(T[] array) {
		if (ArrayUtil.isNotEmpty(array)) {
			for (Object bean : array) {
				nullToEmpty(bean);
			}
		}
		return array;
	}

	/**
	 * 修改注解中的值
	 * 
	 * @param annotation 注解
	 * @param k          方法名
	 * @param v          值
	 */
	public static void putAnnotationValue(Annotation annotation, String k, Object v) {
		try {
			InvocationHandler invocationHandler = Proxy.getInvocationHandler(annotation);
			Field value = invocationHandler.getClass().getDeclaredField("memberValues");
			value.setAccessible(true);
			@SuppressWarnings("unchecked")
			Map<String, Object> memberValues = (Map<String, Object>) value.get(invocationHandler);
			memberValues.put(k, v);
		} catch (Exception e) {
			throw new BusinessException(ExceptionUtil.getMessage(e));
		}
	}

	/**
	 * 重新赋值 private final 修饰的属性
	 * 
	 * @param obj       原始对象
	 * @param fieldName 属性名字
	 * @param value     值
	 * @throws Exception 反射中的异常
	 */
	public static void setPrivateFinalField(Object obj, String fieldName, Object value) throws Exception {
		Field field = ReflectUtil.getField(obj instanceof Class ? (Class<?>) obj : obj.getClass(), fieldName);
		field.setAccessible(true);
		Field modifersField = Field.class.getDeclaredField("modifiers");
		modifersField.setAccessible(true);
		// 把指定的field中的final修饰符去掉
		modifersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
		field.set(obj, value);
	}

	/**
	 * 移除左对象中和右边相同的属性值（用于判断对象是否发生改变）
	 * 
	 * @param left  操作的对象
	 * @param right 对比的对象
	 */
	public static void leftDuplicateRemoval(Object left, Object right) {
		// 取差集
		Field[] fields = ReflectUtil.getFields(left.getClass());
		for (Field field : fields) {
			Object leftVal = BeanUtil.getFieldValue(left, field.getName());
			Object rightVal = BeanUtil.getFieldValue(right, field.getName());
			if (!"serialVersionUID".equals(field.getName()) && ObjectUtil.isNotNull(leftVal)
					&& leftVal.equals(rightVal)) {
				BeanUtil.setFieldValue(left, field.getName(), null);
			}
		}
	}

	/**
	 * 转换list
	 * 
	 * @param <S>    源参数泛型
	 * @param <T>    目标参数泛型
	 * @param source 要转换的list
	 * @param clazz  需要转换的 class
	 * @return {@link List}
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
	 * 1.解析树形数据-实现接口型 递归算法 实体类需要实现 {@link TreeEntity}
	 * 
	 * @param <E>             entity泛型
	 * @param entityList（数据源）
	 * @return {@link TreeEntity}
	 */
	public static <E extends TreeEntity<E>> List<E> toTreeList(List<E> entityList) {
		if (CollUtil.isEmpty(entityList)) {
			return Collections.emptyList();
		}
		// 数据整合-根据id 组装成 HashSet
		Set<Serializable> idSet = entityList.stream().map(TreeEntity::getId).collect(Collectors.toSet());
		// 获取顶层元素集合
		List<E> rootList = entityList.stream().filter(e -> {
			Serializable parentId = e.getParentId();
			return StrUtil.isBlankIfStr(parentId) || !idSet.contains(parentId);
		}).collect(Collectors.toList());
		// 数据整合-根据parentId 装成 HashMap
		Map<Serializable, List<E>> parentIdMap = entityList.stream().collect(Collectors.groupingBy(e -> {
			Serializable parentId = e.getParentId();
			return parentId != null ? parentId : StrUtil.EMPTY;
		}));
		// 递归获取每个顶层元素的子数据集合
		rootList.forEach(r -> recursiveFill(r, parentIdMap));
		return rootList;
	}

	/**
	 * 1.解析树形数据-注解型 递归算法 实体类需要增加注解 {@link TreeAnnotation}
	 * 
	 * @param <T>             目标参数泛型
	 * @param entityList（数据源）
	 * @return {@link List}
	 */
	public static <T> List<T> toTreeAnnoList(List<T> entityList) {
		if (CollUtil.isEmpty(entityList)) {
			return Collections.emptyList();
		}
		// 校验实体类是否具备解析条件
		Map<TreeEnum, Field> treeMap = TreeEnum.convertTreeMap(entityList.get(0).getClass());
		// 数据整合-根据id 组装成 HashSet
		Set<Serializable> idSet = entityList.stream()
				.map(e -> (Serializable) ReflectUtil.getFieldValue(e, treeMap.get(TreeEnum.ID)))
				.collect(Collectors.toSet());
		// 获取顶层元素集合
		List<T> rootList = entityList.stream().filter(e -> {
			Object parentId = ReflectUtil.getFieldValue(e, treeMap.get(TreeEnum.PARENT_ID));
			return StrUtil.isBlankIfStr(parentId) || !idSet.contains(parentId);
		}).collect(Collectors.toList());
		// 数据整合-根据parentId 装成 HashMap
		Map<Serializable, List<T>> parentIdMap = entityList.stream().collect(Collectors.groupingBy(e -> {
			Serializable parentId = (Serializable) ReflectUtil.getFieldValue(e, treeMap.get(TreeEnum.PARENT_ID));
			return parentId != null ? parentId : StrUtil.EMPTY;
		}));
		// 递归获取每个顶层元素的子数据集合
		rootList.forEach(r -> recursiveFill(r, treeMap, parentIdMap));
		return rootList;
	}

	/**
	 * 递归填充子集
	 * 
	 * @param <E>         目标参数泛型
	 * @param root        根对象
	 * @param parentIdMap 数据源
	 * @return {@link TreeEntity}
	 */
	public static <E extends TreeEntity<E>> E recursiveFill(E root, Map<Serializable, List<E>> parentIdMap) {
		Object parentId = root.getId();
		Assert.notNull(parentId, "[Id 异常：{}]", root);
		// 循环并获取子集对象
		List<E> children = parentIdMap.get(parentId);
		if (children == null) {
			children = Collections.emptyList();
		}
		// 递归子集
		children.forEach(s -> recursiveFill(s, parentIdMap));
		// 返回
		root.setSubList(children);
		return root;
	}

	/**
	 * 递归填充子集
	 * 
	 * @param root        根对象
	 * @param treeMap     字段属性的必要条件
	 * @param parentIdMap 以parentId 为key的List集合
	 * 
	 */
	/**
	 * 递归填充子集
	 * 
	 * @param <T>         目标参数泛型
	 * @param root        根对象
	 * @param treeMap     treeMap
	 * @param parentIdMap 数据源
	 * @return {@link TreeEntity}
	 */
	public static <T> T recursiveFill(T root, Map<TreeEnum, Field> treeMap, Map<Serializable, List<T>> parentIdMap) {
		Object parentId = ReflectUtil.getFieldValue(root, treeMap.get(TreeEnum.ID));
		Assert.notNull(parentId, "[Id 异常：{}]", root);
		// 循环并获取子集对象
		List<T> children = parentIdMap.get(parentId);
		if (children == null) {
			children = Collections.emptyList();
		}
		// 递归子集
		children.forEach(s -> recursiveFill(s, treeMap, parentIdMap));
		// 返回
		ReflectUtil.setFieldValue(root, treeMap.get(TreeEnum.SUB_LIST), children);
		return root;
	}

	/**
	 * 将list转为树结构map
	 * 
	 * @param <E>          目标参数泛型
	 * @param entityList   目标集合
	 * @param topId        顶级id
	 * @param idName       id名称
	 * @param parentIdName 父级id名称
	 * @param childrenName 子集名称
	 * @return map集合
	 */
	public static <E> List<Map<String, Object>> toMapTree(List<E> entityList, Serializable topId, String idName,
			String parentIdName, String childrenName) {
		if (CollUtil.isEmpty(entityList)) {
			return Collections.emptyList();
		}
		final List<Map<String, Object>> collect = entityList.stream().map(BeanUtil::beanToMap)
				.collect(Collectors.toList());
		return collect.stream().map(e -> {
			final Object obj = e.get(parentIdName);
			if (StrUtil.isBlankIfStr(obj)) {
				return null;
			}
			final String parentId = obj.toString();
			if (parentId.equals(topId)) {
				getChildren(collect, e, idName, parentIdName, childrenName);
				return e;
			}
			return null;
		}).filter(Objects::nonNull).collect(Collectors.toList());
	}

	/**
	 * 获取字集
	 *
	 * @param entityList   目标集合
	 * @param parentMap    父集合
	 * @param idName       id名
	 * @param parentIdName 父级id名称
	 * @param childrenName 子集名称
	 */
	public static void getChildren(List<Map<String, Object>> entityList, Map<String, Object> parentMap, String idName,
			String parentIdName, String childrenName) {
		final Object id = parentMap.get(idName);
		final List<Map<String, Object>> collect = entityList.stream().map(e -> {
			final Object parentId = e.get(parentIdName);
			if (parentId.equals(id)) {
				getChildren(entityList, e, idName, parentIdName, childrenName);
				return e;
			}
			return null;
		}).filter(Objects::nonNull).collect(Collectors.toList());
		parentMap.put(childrenName, collect);
	}

}