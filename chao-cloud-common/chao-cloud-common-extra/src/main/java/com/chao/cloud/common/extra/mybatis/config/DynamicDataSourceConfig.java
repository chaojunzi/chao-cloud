package com.chao.cloud.common.extra.mybatis.config;

import java.lang.reflect.Method;
import java.util.Map;

import org.springframework.aop.ClassFilter;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.DynamicMethodMatcherPointcut;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DataSourceProperty;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceProperties;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourcePropertiesCustomizer;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.toolkit.JdbcUtils;
import com.chao.cloud.common.extra.mybatis.config.DynamicTableProperties.DynamicRule;
import com.chao.cloud.common.extra.mybatis.constant.DBContext;
import com.chao.cloud.common.util.ConfigUtil;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@ConditionalOnProperty(prefix = DynamicDataSourceProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
public class DynamicDataSourceConfig {

	// aop拦截所有service
	@Bean
	public DynamicDataSourceInterceptor dynamicDataSourceInterceptor() {
		return new DynamicDataSourceInterceptor();
	}

	@Bean
	public DefaultPointcutAdvisor dynamicDataSourcePointcutAdvisor(DynamicDataSourceInterceptor interceptor,
			DynamicTableProperties prop) {
		DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(new DynamicMethodMatcherPointcut() {
			@Override
			public ClassFilter getClassFilter() {
				return clazz -> {
					return ClassUtil.isAssignable(IService.class, clazz);
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

	// 动态数据源
	@Bean
	public DynamicDataSourcePropertiesCustomizer sqliteDynamicDataSourceCustomizer(
			DynamicTableProperties dynamicTableProperties) {
		return properties -> {
			Map<String, DataSourceProperty> datasource = properties.getDatasource();
			Map<String, DynamicRule> ruleMap = dynamicTableProperties.getDatasource();
			datasource.forEach((name, prop) -> {
				// 修改连接配置
				String url = prop.getUrl();
				DbType dbType = JdbcUtils.getDbType(url);
				switch (dbType) {
				case SQLITE:
					try {
						String dbPath = ConfigUtil.getMainFileName(url);
						if (StrUtil.isNotBlank(dbPath)) {
							log.info("【数据库-{}】: {}", name, dbPath);
							url = StrUtil.format("jdbc:sqlite:{}", dbPath);
							prop.setUrl(url);
							DBContext.addDBFile(dbPath);
							// 规则同步
							if (ruleMap.containsKey(name)) {
								DynamicRule rule = ruleMap.get(name);
								rule.setUrl(url);
							}
						}
					} catch (Exception e) {
						log.info("配置文件读取异常");
						throw ExceptionUtil.wrapRuntime(e);
					}
					break;
				default:
					break;
				}
			});
		};
	}

}
