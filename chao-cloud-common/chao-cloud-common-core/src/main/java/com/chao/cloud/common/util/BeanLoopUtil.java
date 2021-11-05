package com.chao.cloud.common.util;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.function.Consumer;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.WeakCache;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.TypeUtil;

/**
 * bean 属性操作
 * 
 * @author 薛超
 * @since 2021年5月21日
 * @version 1.0.9
 */
public final class BeanLoopUtil {

	private static final WeakCache<Class<?>, Class<?>> BEAN_TYPE_CACHE = CacheUtil.newWeakCache(30000L);

	/**
	 * 递归操作集合class<br>
	 * 注：允许互相引用 <br>
	 * 1.A->B->A <br>
	 * 2.A->A
	 * 
	 * @param beanType         实体类类型
	 * @param collTypeConsumer 集合中的class类型
	 */
	/**
	 * 递归操作集合class<br>
	 * 注：允许互相引用 <br>
	 * 1.A->B->A <br>
	 * 2.A->A
	 * 
	 * @param beanType         实体类类型
	 * @param beanTypeConsumer 集合中的class类型
	 */
	public static void loopBeanType(Class<?> beanType, Consumer<Class<?>> beanTypeConsumer) {
		if (ClassUtil.isSimpleTypeOrArray(beanType)) {
			return;
		}
		if (BeanUtil.isBean(beanType)) {
			if (BEAN_TYPE_CACHE.containsKey(beanType)) {
				return;
			}
			// 处理
			beanTypeConsumer.accept(beanType);
			BEAN_TYPE_CACHE.put(beanType, beanType);
			// 遍历bean中的属性
			Field[] fields = ReflectUtil.getFields(beanType);
			for (Field field : fields) {
				Class<?> sourceType = TypeUtil.getClass(field);
				if (!ClassUtil.isAssignable(Collection.class, sourceType)) {
					// 深层递归
					loopBeanType(sourceType, beanTypeConsumer);
					continue;
				}
				// 处理集合类型
				Type type = TypeUtil.getTypeArgument(field.getGenericType(), 0);
				if (type != null) {
					Class<?> collType = TypeUtil.getClass(type);
					// 再次递归
					loopBeanType(collType, beanTypeConsumer);
				}
			}
		}
	}

}
