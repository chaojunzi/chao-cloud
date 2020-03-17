package com.chao.cloud.common.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.log.StaticLog;

/**
 * 获取容器中的资源
 * 
 * @author 薛超
 * @since 2019年8月1日
 * @version 1.0.5
 */
public final class ApplicationOperation {

	/**
	 * 根据接口获取所有的实现类
	 * 
	 * @param <T>      bean 泛型
	 * @param beanType bean 类型
	 * @return List
	 */
	public static <T> List<T> getInterfaceImplClass(Class<T> beanType) {
		Map<String, T> beansOfType = SpringUtil.getApplicationContext().getBeansOfType(beanType);
		Collection<T> values = beansOfType.values();
		return new ArrayList<>(values);
	}

	/**
	 * 移除bean
	 * 
	 * @param beanId ID
	 */
	public static void unregisterBean(String beanId) {
		getBeanDefinitionRegistry().removeBeanDefinition(beanId);
	}

	public static void registerBean(Class<?> clazz) {
		// 获取classname
		String className = clazz.getName();
		// classid
		String simpleName = clazz.getSimpleName();
		String beanId = StrUtil.lowerFirst(simpleName);
		registerBeanDefinition(beanId, className);
	}

	public static void registerBean(String beanId, Class<?> clazz) {
		registerBeanDefinition(beanId, clazz);
	}

	public static void registerBean(String beanId, BeanDefinition beanDefinition) {
		getBeanDefinitionRegistry().registerBeanDefinition(beanId, beanDefinition);
	}

	/**
	 * 注册bean
	 * 
	 * @param beanId    所注册bean的id
	 * @param className bean的className， 三种获取方式： 1.直接书写，如：com.mvc.entity.User
	 *                  2.User.class.getName 3.user.getClass().getName()
	 */
	public static void registerBeanDefinition(String beanId, String className) {
		// get the BeanDefinitionBuilder
		BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(className);
		// get the BeanDefinition
		BeanDefinition beanDefinition = beanDefinitionBuilder.getBeanDefinition();
		// register the bean
		getBeanDefinitionRegistry().registerBeanDefinition(beanId, beanDefinition);
	}

	public static void registerBeanDefinition(String beanId, Class<?> clazz) {
		// get the BeanDefinitionBuilder
		BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(clazz);
		// get the BeanDefinition
		BeanDefinition beanDefinition = beanDefinitionBuilder.getBeanDefinition();
		// register the bean
		getBeanDefinitionRegistry().registerBeanDefinition(beanId, beanDefinition);
	}

	public static BeanDefinitionRegistry getBeanDefinitionRegistry() {
		return (DefaultListableBeanFactory) ((ConfigurableApplicationContext) SpringUtil.getApplicationContext())
				.getBeanFactory();
	}

	/**
	 * 销毁所有的实现类
	 * 
	 * @param beanType bean类型
	 * @return boolean
	 */
	public static boolean destroyBeans(Class<?> beanType) {
		try {
			String[] beanNames = SpringUtil.getApplicationContext().getBeanNamesForType(beanType);
			for (String name : beanNames) {
				// 销毁
				unregisterBean(name);
			}
		} catch (Exception e) {
			StaticLog.error(e);
			return false;
		}
		return true;
	}

	/**
	 * 获取所有controller路径
	 * 
	 * @return Map
	 */
	public static Map<String, Set<String>> mappingHandlerUrl() {
		Map<String, Set<String>> resp = new HashMap<>();
		Map<String, RequestMappingHandlerMapping> beansOfType = SpringUtil.getApplicationContext()
				.getBeansOfType(RequestMappingHandlerMapping.class);
		Set<Entry<String, RequestMappingHandlerMapping>> entrySet = beansOfType.entrySet();
		RequestMappingHandlerMapping mapping = null;
		for (Entry<String, RequestMappingHandlerMapping> entry : entrySet) {
			String key = entry.getKey();
			mapping = entry.getValue();
			Map<RequestMappingInfo, HandlerMethod> handlerMethods = mapping.getHandlerMethods();
			Set<String> value = new HashSet<String>();
			for (RequestMappingInfo rmi : handlerMethods.keySet()) {
				PatternsRequestCondition pc = rmi.getPatternsCondition();
				Set<String> pSet = pc.getPatterns();
				value.addAll(pSet);
			}
			resp.put(key, value);
		}
		return resp;
	}

	public static Set<String> allMappingHandlerUrl() {
		Set<String> value = new HashSet<String>();
		List<RequestMappingHandlerMapping> list = getInterfaceImplClass(RequestMappingHandlerMapping.class);
		for (RequestMappingHandlerMapping mapping : list) {
			Map<RequestMappingInfo, HandlerMethod> handlerMethods = mapping.getHandlerMethods();
			for (RequestMappingInfo rmi : handlerMethods.keySet()) {
				PatternsRequestCondition pc = rmi.getPatternsCondition();
				Set<String> pSet = pc.getPatterns();
				value.addAll(pSet);
			}
		}
		return value;
	}
}