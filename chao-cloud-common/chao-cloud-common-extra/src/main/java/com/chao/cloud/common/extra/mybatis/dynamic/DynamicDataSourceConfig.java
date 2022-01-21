package com.chao.cloud.common.extra.mybatis.dynamic;

import java.util.Map;

import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DataSourceProperty;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourcePropertiesCustomizer;
import com.chao.cloud.common.extra.mybatis.annotation.MybatisPlusConfig;
import com.chao.cloud.common.util.ConfigUtil;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class DynamicDataSourceConfig {

	// aop拦截所有service
	@Bean
	public DynamicDataSourceInterceptor dynamicDataSourceInterceptor() {
		return new DynamicDataSourceInterceptor();
	}

	@Bean
	public DefaultPointcutAdvisor dynamicDataSourcePointcutAdvisor(DynamicDataSourceInterceptor interceptor,
			DynamicTableRuleProperties prop) {
		DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(new DynamicDataSourceMatcherPointcut(),
				interceptor);
		advisor.setOrder(prop.getOrder());
		return advisor;
	}

	// 动态数据源
	@Bean
	public DynamicDataSourcePropertiesCustomizer sqliteDynamicDataSourceCustomizer() {
		return properties -> {
			Map<String, DataSourceProperty> datasource = properties.getDatasource();
			datasource.forEach((name, prop) -> {
				if ("org.sqlite.JDBC".equals(prop.getDriverClassName())) {
					// 修改连接配置
					String url = prop.getUrl();
					try {
						String dbPath = ConfigUtil.getMainFileName(url);
						if (StrUtil.isNotBlank(dbPath)) {
							log.info("【数据库-{}】: {}", name, dbPath);
							prop.setUrl(StrUtil.format("jdbc:sqlite:{}", dbPath));
							MybatisPlusConfig.getDbFileList().add(dbPath);
						}
					} catch (Exception e) {
						log.info("配置文件读取异常");
						throw ExceptionUtil.wrapRuntime(e);
					}
				}
			});
		};
	}

}
