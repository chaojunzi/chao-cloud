package com.chao.cloud.common.extra.mybatis.config;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.aop.ClassFilter;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.DynamicMethodMatcherPointcut;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.baomidou.mybatisplus.core.mapper.Mapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.chao.cloud.common.extra.mybatis.config.ShardingTableProperties.ShardingTableRule;
import com.chao.cloud.common.extra.mybatis.plugin.MysqlTableNodesComplete;
import com.chao.cloud.common.extra.mybatis.plugin.SqliteTableNodesComplete;
import com.chao.cloud.common.extra.mybatis.sharding.DateMapperInterceptor;
import com.chao.cloud.common.extra.mybatis.sharding.MapperInvoker;
import com.chao.cloud.common.extra.mybatis.sharding.MyShardingTableInterceptor;
import com.chao.cloud.common.extra.mybatis.sharding.ShardingTableHandler;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.EnableSpringUtil;
import cn.hutool.extra.spring.SpringUtil;
import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = Constants.MYBATIS_PLUS)
@EnableConfigurationProperties({ MyDataSourceProperties.class, DynamicTableProperties.class,
		ShardingTableProperties.class })
@EnableSpringUtil
public class SingleDataSourceConfig {

	@Bean
	public MybatisPlusInterceptor mybatisPlusInterceptor(MyShardingTableInterceptor shardingTableInterceptor
	// ObjectProvider<DynamicTableProperties> tableRuleProvider
	) {
		MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
		// 1.自动表名(若不放于第一位，则分页插件将冲突)
		// DynamicTableProperties prop = tableRuleProvider.getIfAvailable();
		// if (prop != null) {
		interceptor.addInnerInterceptor(shardingTableInterceptor);
		// }
		// 2.分页插件
		PaginationInnerInterceptor pageInterceptor = new PaginationInnerInterceptor();
		pageInterceptor.setMaxLimit(-1L);
		interceptor.addInnerInterceptor(pageInterceptor);
		return interceptor;
	}

	@Bean
	public MyShardingTableInterceptor myShardingTableInterceptor(ShardingTableProperties prop) {
		return new MyShardingTableInterceptor(this.buildShardingTableHandlerMap(prop));
	}

	private Map<String, ShardingTableHandler> buildShardingTableHandlerMap(ShardingTableProperties prop) {
		Map<String, ShardingTableHandler> shardingTableHandlerMap = MapUtil.newHashMap();
		Map<String, ShardingTableRule> shardingTableMap = prop.getShardingTableRule();
		// 不启用分表功能
		if (!prop.isEnabled() || MapUtil.isEmpty(shardingTableMap)) {
			return shardingTableHandlerMap;
		}

		shardingTableMap.forEach((t, rule) -> {
			shardingTableHandlerMap.put(t, rule.getType().buildTableNameHandler(rule));
		});
		return shardingTableHandlerMap;
	}

	/**
	 * -----------------------自动分片处理----------------------- aop拦截所有mapper
	 */
	@Bean
	public DateMapperInterceptor dateMapperInterceptor() {
		// 扫描当前配置下的mapper实现类
		String packageName = ClassUtil.getPackage(getClass());
		Map<String, MapperInvoker> mapperMap = SpringUtil.getBeansOfType(MapperInvoker.class);
		List<MapperInvoker> mapperInvokers = CollUtil.newArrayList();
		if (CollUtil.isNotEmpty(mapperMap)) {
			mapperInvokers.addAll(mapperMap.values());
		}
		// 扫描实现类
		Set<Class<?>> mapperClassSet = ClassUtil.scanPackageBySuper(packageName, MapperInvoker.class);
		if (CollUtil.isNotEmpty(mapperClassSet)) {
			List<Class<?>> mapperClassList = CollUtil.map(mapperInvokers, MapperInvoker::getClass, true);
			mapperClassSet.stream()//
					.filter(m -> !mapperClassList.contains(m) && ClassUtil.isNormalClass(m))//
					.forEach(m -> {
						MapperInvoker invoker = (MapperInvoker) ReflectUtil.newInstance(m);
						// 注册到spring容器
						String className = ClassUtil.getClassName(m, true);
						SpringUtil.registerBean(StrUtil.format("default-{}", className), invoker);
						mapperInvokers.add(invoker);
					});
		}
		return new DateMapperInterceptor(mapperInvokers);
	}

	@Bean
	public DefaultPointcutAdvisor dataRangeMapperPointcutAdvisor(DateMapperInterceptor interceptor,
			ShardingTableProperties prop) {
		DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(new DynamicMethodMatcherPointcut() {
			@Override
			public ClassFilter getClassFilter() {
				return clazz -> {
					return ClassUtil.isAssignable(Mapper.class, clazz);
				};
			}

			@Override
			public boolean matches(Method method, Class<?> targetClass, Object... args) {
				return true;
			}
		}, interceptor);
		advisor.setOrder(prop.getOrder());
		return advisor;
	}

	/**
	 * -------------------------生成表节点-------------------------
	 */
	@Bean
	public SqliteTableNodesComplete sqliteTableNodesComplete() {
		return new SqliteTableNodesComplete();
	}

	@Bean
	public MysqlTableNodesComplete mysqlTableNodesComplete() {
		return new MysqlTableNodesComplete();
	}
}
