package com.chao.cloud.common.util;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.chao.cloud.common.annotation.FieldAlias;
import com.chao.cloud.common.annotation.TreeAnnotation;
import com.chao.cloud.common.constant.TreeEnum;
import com.chao.cloud.common.entity.TreeEntity;
import com.chao.cloud.common.exception.BusinessException;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.convert.Converter;
import cn.hutool.core.convert.ConverterRegistry;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.lang.func.Func1;
import cn.hutool.core.lang.func.LambdaUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.stream.StreamUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ModifierUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

/**
 * 实体转换
 * 
 * @author 薛超
 * @since 2019年8月28日
 * @version 1.0.7
 */
public final class EntityUtil {

	public static final CopyOptions COPY_OPTIONS = CopyOptions.create()//
			.setIgnoreError(true)//
			.setIgnoreCase(true)//
			.setIgnoreNullValue(true);
	/**
	 * bean属性拷贝映射
	 */
	private static final Map<Class<?>, WeakReference<CopyOptions>> BEAN_OPTIONS_MAP = new ConcurrentHashMap<>();
	/**
	 * 自定义的别名注解;须有value() 方法；否则不生效
	 */
	private static final List<Class<? extends Annotation>> FIELD_ALIAS_ANNO_LIST = CollUtil.newArrayList();

	static {
		// 设置别名注解
		setFieldAliasAnno(FieldAlias.class);
	}

	public static synchronized void setFieldAliasAnno(Class<? extends Annotation> anno) {
		Assert.notNull(anno, "属性映射注解不能为空");
		if (FIELD_ALIAS_ANNO_LIST.contains(anno)) {
			return;
		}
		FIELD_ALIAS_ANNO_LIST.add(anno);
	}

	/**
	 * xml转bean
	 * 
	 * @param <T>      bean泛型
	 * @param xml      xml
	 * @param beanType 实体类型
	 * @return 转换后的bean
	 */
	public static <T> T xmlToBean(String xml, Class<T> beanType) {
		return xmlToBean(xml, beanType, null);
	}

	public static <T> T xmlToBean(String xml, Class<T> beanType, String expression) {
		Assert.notBlank(xml, "xml 不能为空");
		JSONObject json = JSONUtil.xmlToJson(xml);
		if (StrUtil.isBlank(expression)) {
			return toBean(json, beanType);
		}
		Object obj = json.getByPath(expression);
		if (ObjectUtil.isNull(obj)) {
			return null;
		}
		return toBean(obj, beanType);
	}

	public static <T> T toBean(Object source, Class<T> beanType) {
		Assert.notNull(source, "source 不能为空");
		Assert.notNull(beanType, "beanType 不能为空");
		// 获取此类转换器
		ConverterRegistry registry = ConverterRegistry.getInstance();
		Converter<T> converter = registry.getCustomConverter(beanType);
		if (converter == null) {
			// 递归类的属性
			BeanLoopUtil.loopBeanType(beanType, type -> {
				registry.putCustom(type, (value, deaultValue) -> {
					CopyOptions options = getCopyOptions(type);
					return BeanUtil.toBean(value, type, options);
				});
			});
		}
		// 开始转换
		return registry.convert(beanType, source);
	}

	public static Map<String, String> getFieldMapping(Class<?> beanType) {
		Map<String, String> fieldMap = MapUtil.newHashMap();
		if (beanType == null) {
			return fieldMap;
		}
		Field[] fields = ReflectUtil.getFields(beanType);
		StreamUtil.of(fields).forEach(f -> {
			// 其他属性
			FIELD_ALIAS_ANNO_LIST.forEach(annoType -> {
				String alias = AnnotationUtil.getAnnotationValue(f, annoType);
				if (StrUtil.isNotBlank(alias)) {
					// 后面的会替换掉新的
					fieldMap.put(alias, f.getName());
				}
			});

		});
		return fieldMap;

	}

	public static <R, T> void setFieldValue(R obj, Class<T> fieldType, T value) {
		setFieldValue(obj, fieldType, value, null);
	}

	public static CopyOptions getCopyOptions(Class<?> beanType) {
		return Optional.ofNullable(BEAN_OPTIONS_MAP.get(beanType))//
				.map(WeakReference::get)//
				.orElseGet(() -> {
					Map<String, String> mapping = getFieldMapping(beanType);
					CopyOptions options = CopyOptions//
							.create()//
							.setFieldMapping(mapping)//
							.ignoreCase()//
							.ignoreError()//
							.ignoreNullValue();
					BEAN_OPTIONS_MAP.put(beanType, new WeakReference<>(options));
					return options;
				});
	}

	/**
	 * 修改第一个属性类型
	 * 
	 * @param <R>       对象类型
	 * @param <T>       值类型
	 * @param obj       基本对象
	 * @param fieldType 属性类型
	 * @param value     属性值
	 * @param consumer  消费
	 */
	public static <R, T> void setFieldValue(R obj, Class<T> fieldType, T value, Consumer<T> consumer) {
		if (obj == null || value == null) {
			return;
		}
		// 获取所有属性
		Class<R> objType = ClassUtil.getClass(obj);
		Field field = ArrayUtil.firstMatch(f -> f.getType() == fieldType, ReflectUtil.getFields(objType));
		//
		if (field != null) {
			ReflectUtil.setFieldValue(obj, field, value);
		}
		if (consumer != null) {
			consumer.accept(value);
		}
	}

	/**
	 * 操作bean中的属性；忽略大小写
	 * 
	 * @param bean      实体类
	 * @param fieldName 属性名称
	 * @param consumer  消费者函数
	 */
	@SuppressWarnings("unchecked")
	public static <T> void funcPropertyField(Object bean, String fieldName, Consumer<T> consumer) {
		editProperty(bean, b -> {
			Field[] fields = ReflectUtil.getFields(b.getClass());
			if (ArrayUtil.isEmpty(fields)) {
				return;
			}
			// 匹配属性
			for (Field field : fields) {
				if (StrUtil.equalsIgnoreCase(field.getName(), fieldName)) {
					// 获取属性值
					Object v = ReflectUtil.getFieldValue(b, field);
					consumer.accept((T) v);
				}
			}
		});
	}

	/**
	 * 获取bean中的属性
	 * 
	 * @param <T>      字段类型
	 * @param bean     实体类
	 * @param fields   字段列表
	 * @param propType 字段类型
	 * @return 字段值
	 */
	public static <T> T getProperty(Object bean, String[] fields, Class<T> propType) {
		for (String fieldName : fields) {
			Object value = get(bean, fieldName);
			if (value != null) {
				return Convert.convert(propType, value);
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static <R, T> T getProperty(R entity, Class<T> propType) {
		if (entity == null || propType == null) {
			return null;
		}
		// 获取所有属性
		Class<R> objType = ClassUtil.getClass(entity);
		Field field = ArrayUtil.firstMatch(f -> f.getType() == propType, ReflectUtil.getFields(objType));
		//
		return (T) ReflectUtil.getFieldValue(entity, field);
	}

	/**
	 * 获取对象-递归算法
	 * 
	 * @param obj 元对象
	 */
	private static Object get(Object bean, String fieldName) {
		if (bean == null) {// back
			return null;
		}
		Class<?> classType = bean.getClass();
		// 集合-----------------------------------------------------
		if (bean instanceof Collection) {
			return getByColl(bean, fieldName);
		}
		// Bean------------------------------------------------------
		if (BeanUtil.isBean(classType)) {
			return getByBean(bean, fieldName);
		}
		return null;
	}

	/**
	 * 获取集合对象属性
	 * 
	 * @param obj       对象
	 * @param fieldName 属性名称
	 * @return 属性值
	 */
	@SuppressWarnings("unchecked")
	private static Object getByColl(Object obj, String fieldName) {
		boolean isSet = obj instanceof Set;
		boolean isList = obj instanceof List;
		if (!isList && !isSet) {
			return null;
		}
		Collection<Object> coll = (Collection<Object>) obj;
		for (Object o : coll) {
			Object v = get(o, fieldName);
			if (v != null) {
				return v;
			}
		}
		return null;
	}

	/**
	 * 获取实体属性
	 * 
	 * @param obj       对象
	 * @param fieldName 属性名称
	 * @return 属性值
	 */
	private static Object getByBean(Object obj, String fieldName) {
		Field[] fields = ReflectUtil.getFields(obj.getClass());
		if (ArrayUtil.isEmpty(fields)) {
			return null;
		}
		for (Field field : fields) {
			if (ModifierUtil.isStatic(field)) {
				continue;
			}
			Object v = ReflectUtil.getFieldValue(obj, field);
			// 忽略大小写
			if (StrUtil.equalsIgnoreCase(fieldName, field.getName())) {
				return v;
			}
			// 判断是否为bean
			if (ClassUtil.isSimpleTypeOrArray(field.getType())) {
				continue;
			}
			if (v == null) {
				continue;
			}
			// 返回值
			Object r = get(v, fieldName);
			if (r != null) {
				return r;
			}

		}
		return null;
	}

	/**
	 * 编辑bean中
	 * 
	 * @param <T>      对象类型
	 * @param bean     实体类
	 * @param consumer 编辑方法
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Object editProperty(Object bean, Consumer<Object> consumer) {
		// 递归bean
		if (bean == null) {
			return null;
		}
		Class<?> beanClass = bean.getClass();
		boolean simpleType = ClassUtil.isSimpleTypeOrArray(beanClass);
		if (simpleType) {
			return bean;
		}
		// 集合
		if (bean instanceof Collection<?>) {
			Collection<Object> coll = (Collection<Object>) bean;
			coll.forEach(o -> editProperty(o, consumer));
			return bean;
		}
		boolean isBean = BeanUtil.isBean(beanClass);
		if (isBean) {
			// 获取属性
			Field[] fields = ReflectUtil.getFields(beanClass);
			for (Field field : fields) {
				Object val = ReflectUtil.getFieldValue(bean, field);
				// 递归
				editProperty(val, consumer);
			}
			// 消费
			consumer.accept(bean);
		}
		return bean;
	}

	/**
	 * 获取属性-根据lambda表达式<br>
	 * 注：此方法对Map可能无效
	 * 
	 * @param <T>    实体类型
	 * @param <R>    返回值类型
	 * @param entity 实体对象
	 * @param func   属性表达式：Entity::getId
	 * @return 属性值
	 */
	public static <T, R> R getProperty(T entity, Func1<T, R> func) {
		if (ObjectUtil.isNull(entity)) {
			return null;
		}
		String methodName = LambdaUtil.getMethodName(func);
		return BeanUtil.getProperty(entity, StrUtil.getGeneralField(methodName));
	}

	public static <T, R> void setProperty(T entity, Func1<T, R> func, R val) {
		if (ObjectUtil.isNull(entity)) {
			return;
		}
		String methodName = LambdaUtil.getMethodName(func);
		BeanUtil.setProperty(entity, StrUtil.getGeneralField(methodName), val);
	}

	@SuppressWarnings("unchecked")
	public static <T, R> R getProperty(T entity, TypeReference<R> type) {
		if (ObjectUtil.isNull(entity)) {
			return null;
		}
		Type t = type.getType();
		Field field = StreamUtil.of(ReflectUtil.getFields(entity.getClass())).filter(f -> f.getGenericType().equals(t))
				.findFirst().orElse(null);
		Assert.notNull(field, "无效的类型:{}", t.getTypeName());
		return (R) ReflectUtil.getFieldValue(entity, field);
	}

	public static <T, R> void setProperty(T entity, TypeReference<R> type, R val) {
		if (ObjectUtil.isNull(entity)) {
			return;
		}
		Type t = type.getType();
		Field field = StreamUtil.of(ReflectUtil.getFields(entity.getClass())).filter(f -> f.getGenericType().equals(t))
				.findFirst().orElse(null);
		Assert.notNull(field, "无效的类型:{}", t.getTypeName());
		ReflectUtil.setFieldValue(entity, field, val);
	}

	/**
	 * 空值转换
	 * 
	 * @param <T>  对象泛型
	 * @param bean 对象实体
	 * @return 对象
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
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

	/**
	 * 判断对象是否不为空
	 * 
	 * @param <T>    对象泛型
	 * @param entity 实体对象
	 * @return true or false
	 */
	public static <T> boolean isNotEmpty(T entity) {
		return !isEmpty(entity);
	}

	/**
	 * 判断对象是否为空
	 * 
	 * @param <T>    对象泛型
	 * @param entity 实体对象
	 * @return true or false
	 */
	public static <T> boolean isEmpty(T entity) {
		if (entity == null) {
			return true;
		}
		String[] ignoreFiledNames = CollUtil.toList(ReflectUtil.getFields(entity.getClass()))//
				.stream()//
				.filter(f -> Modifier.isStatic(f.getModifiers()))//
				.map(Field::getName)//
				.distinct()//
				.toArray(String[]::new);
		return BeanUtil.isEmpty(entity, ignoreFiledNames);// 排除静态属性
	}

	/**
	 * 对象属性覆盖
	 * 
	 * @param <T>       目标对象类型
	 * @param source    源对象
	 * @param beanClass 目标对象类型
	 * @return 目标对象
	 */
	public static <T> T fieldCopy(Object source, Class<T> beanClass) {
		return fieldCopy(source, beanClass, CopyOptions.create().ignoreCase(), true);
	}

	/**
	 * 对象属性覆盖
	 * 
	 * @param <T>       目标对象类型
	 * @param source    源对象
	 * @param beanClass 目标对象类型
	 * @param options   {@link CopyOptions}
	 * @param isDown    是否递归
	 * @return 目标对象
	 */
	public static <T> T fieldCopy(Object source, Class<T> beanClass, CopyOptions options, boolean isDown) {
		T t = ReflectUtil.newInstance(beanClass);
		BeanUtil.copyProperties(source, t, options);
		if (!isDown) {// 是否向下-递归
			return t;
		}
		Field[] fields = ReflectUtil.getFields(beanClass);
		if (fields == null) {
			return t;
		}
		for (Field field : fields) {
			if (ModifierUtil.isStatic(field)) {
				continue;
			}
			Class<?> beanType = field.getType();
			if (ClassUtil.isSimpleTypeOrArray(beanType))
				continue;
			if (BeanUtil.isBean(beanType)) {
				Object v = fieldCopy(source, beanType);
				ReflectUtil.setFieldValue(t, field, v);
			}
		}
		return t;
	}
}