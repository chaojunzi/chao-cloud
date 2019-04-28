package com.chao.cloud.common.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * 获取容器中的资源
 * @功能：
 * @author： 薛超
 * @时间：2019年3月1日
 * @version 2.0
 */
public class ApplicationOperation {

    /**
     * 根据接口获取所有的实现类
     */
    public static <T> List<T> getInterfaceImplClass(Class<T> beanType) {
        Map<String, T> beansOfType = SpringContextUtil.getApplicationContext().getBeansOfType(beanType);
        Collection<T> values = beansOfType.values();
        return new ArrayList<>(values);
    }

    /**
     * 移除bean
     * 
     * @param beanId
     *            bean的id
     */
    public static void unregisterBean(String beanId) {
        getBeanDefinitionRegistry().removeBeanDefinition(beanId);
    }

    /**
     * 注册bean
     * 
     * @param beanId
     *            所注册bean的id
     * @param className
     *            bean的className， 三种获取方式：1、直接书写，如：com.mvc.entity.User
     *            2、User.class.getName 3.user.getClass().getName()
     */
    public static void registerBean(String beanId, String className) {
        // get the BeanDefinitionBuilder
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(className);
        // get the BeanDefinition
        BeanDefinition beanDefinition = beanDefinitionBuilder.getBeanDefinition();
        // register the bean
        getBeanDefinitionRegistry().registerBeanDefinition(beanId, beanDefinition);
    }

    public static BeanDefinitionRegistry getBeanDefinitionRegistry() {
        return (DefaultListableBeanFactory) ((ConfigurableApplicationContext) SpringContextUtil.getApplicationContext())
                .getBeanFactory();
    }

    /**
     * 销毁所有的实现类
     */
    public static boolean destroyBeans(Class<?> beanType) {
        try {
            String[] beanNames = SpringContextUtil.getApplicationContext().getBeanNamesForType(beanType);
            for (String name : beanNames) {
                // 销毁
                unregisterBean(name);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}