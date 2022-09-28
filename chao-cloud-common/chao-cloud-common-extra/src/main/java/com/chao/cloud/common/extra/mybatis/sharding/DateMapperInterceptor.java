package com.chao.cloud.common.extra.mybatis.sharding;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.List;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ClassUtils;

import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.override.MybatisMapperProxy;
import com.chao.cloud.common.extra.mybatis.config.ShardingTableProperties;
import com.chao.cloud.common.extra.mybatis.config.ShardingTableProperties.ShardingTableRule;
import com.chao.cloud.common.extra.mybatis.constant.MapperEnum;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.TypeUtil;
import lombok.RequiredArgsConstructor;

/**
 * 根据时间分片多次处理并汇总
 * 
 * @author 薛超
 * @since 2022年5月13日
 * @version 1.0.0
 */
@RequiredArgsConstructor
public class DateMapperInterceptor implements MethodInterceptor {

	private final List<MapperInvoker> mapperInvokers;

	@Autowired
	private ShardingTableProperties properties;

	@SuppressWarnings("rawtypes")
	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		//
		if (!properties.isEnabled()) {
			return invocation.proceed();
		}
		//
		Object target = this.getTarget(invocation.getThis());
		if (!(target instanceof MybatisMapperProxy)) {
			return invocation.proceed();
		}
		Class<?> mapperClass = BeanUtil.getProperty(target, MapperInvoker.mapperField);
		if (mapperClass == null) {
			return invocation.proceed();
		}
		Type type = TypeUtil.getTypeArgument(mapperClass);
		Class<?> entityClass = TypeUtil.getClass(type);
		if (entityClass == null) {
			return invocation.proceed();
		}
		TableInfo tableInfo = TableInfoHelper.getTableInfo(entityClass);
		if (tableInfo == null) {
			return invocation.proceed();
		}
		String tableName = tableInfo.getTableName();
		// 判断表名是否有数据分片规则（日期）
		ShardingTableRule rule = properties.getShardingTableRule().get(tableName);
		if (rule == null || StrUtil.isBlank(rule.getColumn()) || rule.getType() != ShardingEnum.DATE) {
			return invocation.proceed();
		}
		Method method = invocation.getMethod();
		MapperEnum mapperMethod = MapperEnum.getMapperMethod(method.getName());
		if (mapperMethod == null) {
			return invocation.proceed();
		}
		MapperInvoker invoker = CollUtil.findOne(mapperInvokers, h -> h.getMapperMethod() == mapperMethod);
		if (invoker != null) {
			return invoker.invoke((MybatisMapperProxy) target, tableInfo, rule, invocation.getArguments(), invocation);
		}
		return invocation.proceed();
	}

	public Object getTarget(Object proxy) throws Exception {
		String className = proxy.getClass().getName();
		if (StrUtil.contains(className, ClassUtils.CGLIB_CLASS_SEPARATOR)) {
			return ReflectUtil.getFieldValue(proxy, "CGLIB$CALLBACK_0");
		}
		if (Proxy.isProxyClass(proxy.getClass())) {
			return ReflectUtil.getFieldValue(proxy, "h");
		}
		return proxy;
	}

}
