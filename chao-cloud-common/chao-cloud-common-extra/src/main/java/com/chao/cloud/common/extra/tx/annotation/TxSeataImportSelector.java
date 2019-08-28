package com.chao.cloud.common.extra.tx.annotation;

import java.util.Properties;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.chao.cloud.common.exception.BusinessException;
import com.chao.cloud.common.util.EntityUtil;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.StrUtil;
import io.seata.config.ConfigType;
import io.seata.config.ConfigurationFactory;
import io.seata.config.nacos.NacosConfiguration;
import io.seata.discovery.registry.nacos.NacosRegistryServiceImpl;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * 分布式事务整合
 * @author 薛超
 * @since 2019年8月28日
 * @version 1.0.7
 */
@Setter
@Slf4j
public class TxSeataImportSelector implements ImportSelector, EnvironmentAware {

	private Environment environment;
	private Properties properties;
	private final String serverAddrKey = "serverAddr";
	private final String namespaceKey = "namespace";
	private final String clusterKey = "cluster";

	@Override
	public String[] selectImports(AnnotationMetadata metadata) {
		Class<Object> className = ClassUtil.loadClass(metadata.getClassName());
		// 排除 DataSourceAutoConfiguration 自动注入
		SpringBootApplication boot = AnnotationUtil.getAnnotation(className, SpringBootApplication.class);
		// 修改值
		Class<?>[] exclude = boot.exclude();
		if (!ArrayUtil.contains(exclude, DataSourceAutoConfiguration.class)) {
			Class<? extends Object>[] excludeClass = ArrayUtil.append(exclude, DataSourceAutoConfiguration.class);
			EntityUtil.putAnnotationValue(boot, "exclude", excludeClass);
		}
		// 获取注解值
		EnableTxSeata txSeata = AnnotationUtil.getAnnotation(className, EnableTxSeata.class);
		// 加载相关配置
		ConfigType type = txSeata.value();
		switch (type) {
		case Nacos:// 0.7.1 只支持nacos
			// 整合seata-nacos
			Properties properties = this.getNacosProp();
			// 获取配置资源
			try {
				ConfigService configService = NacosFactory.createConfigService(properties);
				// NacosConfiguration
				EntityUtil.setPrivateFinalField(NacosConfiguration.class, "configService", configService);
				//
				NacosConfiguration configuration = new NacosConfiguration();
				// ConfigurationFactory
				EntityUtil.setPrivateFinalField(ConfigurationFactory.class, "CONFIG_INSTANCE", configuration);
				// 修改注册-nacos
				EntityUtil.setPrivateFinalField(ConfigurationFactory.class, "DEFAULT_FILE_INSTANCE", configuration);
				EntityUtil.setPrivateFinalField(ConfigurationFactory.class, "CURRENT_FILE_INSTANCE", configuration);
				// 修改服务发现
				NamingService namingService = NamingFactory.createNamingService(properties);
				EntityUtil.setPrivateFinalField(NacosRegistryServiceImpl.class, "naming", namingService);
			} catch (Exception e) {
				log.error("{}", e);
				throw new BusinessException("Nacos-Seata 初始化失败");
			}
			return new String[] { TxSeataConfig.class.getName() };
		default:
			break;
		}
		return null;
	}

	/**
	 * 获取nacos配置
	 */
	private Properties getNacosProp() {
		if (this.properties != null) {
			return this.properties;
		}
		Properties properties = new Properties();
		// 从环境变量中获取nacos地址
		String serverAddr = StrUtil.nullToDefault(//
				environment.getProperty(StrUtil.format("{}.{}", EnableTxSeata.TX_SEATA_PREFIX, serverAddrKey)), //
				System.getProperty("spring.cloud.nacos.config.server-addr"));
		String namespace = environment
				.getProperty(StrUtil.format("{}.{}", EnableTxSeata.TX_SEATA_PREFIX, namespaceKey));
		String cluster = environment.getProperty(StrUtil.format("{}.{}", EnableTxSeata.TX_SEATA_PREFIX, clusterKey));
		Assert.notBlank(serverAddr, "请配置：{}.{}", EnableTxSeata.TX_SEATA_PREFIX, serverAddrKey);
		properties.put(serverAddrKey, serverAddr);
		properties.put(namespaceKey, StrUtil.nullToDefault(namespace, StrUtil.EMPTY));
		properties.put(clusterKey, StrUtil.nullToDefault(cluster, "default"));
		this.properties = properties;
		return this.properties;
	}

}
