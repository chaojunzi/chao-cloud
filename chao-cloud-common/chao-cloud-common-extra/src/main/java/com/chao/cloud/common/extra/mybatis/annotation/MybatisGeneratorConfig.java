package com.chao.cloud.common.extra.mybatis.annotation;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.FileOutConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.TemplateConfig;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.chao.cloud.common.extra.mybatis.generator.ZipAutoGenerator;
import com.chao.cloud.common.extra.mybatis.generator.engine.ZipVelocityTemplateEngine;
import com.chao.cloud.common.extra.mybatis.generator.template.BeforeConfig;

/**
 * 代码生成器
 * @功能：
 * @author： 薛超
 * @时间：2019年5月20日
 * @version 2.0
 */
@Configuration
public class MybatisGeneratorConfig {

	private String author = "薛超";

	private final static String GLOBAL_CONFIG_PREFIX = "chao.cloud.codegen.global";
	private final static String BEFORE_CONFIG_PREFIX = "chao.cloud.codegen.before";
	private final static String DATA_SOURCE_CONFIG_PREFIX = "chao.cloud.codegen.datasource";
	private final static String PACKAGE_CONFIG_PREFIX = "chao.cloud.codegen.package";
	private final static String TEMPLATE_CONFIG_PREFIX = "chao.cloud.codegen.template";
	private final static String STRATEGY_CONFIG_PREFIX = "chao.cloud.codegen.strategy";

	/**
	 * 全局代码生成器
	 * @return
	 */
	@Bean
	public ZipAutoGenerator autoGenerator(//
			GlobalConfig gc, DataSourceConfig dsc, //
			PackageConfig pc, InjectionConfig cfg, //
			TemplateConfig tc, StrategyConfig sc) {
		// 配置
		ZipAutoGenerator mpg = new ZipAutoGenerator();
		mpg.setGlobalConfig(gc);
		mpg.setDataSource(dsc);
		mpg.setPackageInfo(pc);
		mpg.setCfg(cfg);
		mpg.setTemplate(tc);
		mpg.setStrategy(sc);
		mpg.setTemplateEngine(new ZipVelocityTemplateEngine());
		return mpg;
	}

	@Bean
	@ConfigurationProperties(prefix = BEFORE_CONFIG_PREFIX)
	public BeforeConfig beforeConfig() {
		return new BeforeConfig();
	}

	@Bean
	@ConfigurationProperties(prefix = GLOBAL_CONFIG_PREFIX)
	public GlobalConfig globalConfig() {
		// 全局配置
		GlobalConfig gc = new GlobalConfig();
		gc.setAuthor(author);
		gc.setOpen(false);
		gc.setFileOverride(true);
		gc.setDateType(DateType.ONLY_DATE);// 仅用date 替代
		// gc.setActiveRecord(true);
		// gc.setBaseColumnList(true);
		// gc.setBaseResultMap(true);
		gc.setServiceName("%sService");
		// gc.setSwagger2(true); 实体属性 Swagger2 注解
		return gc;
	}

	@Bean
	@ConfigurationProperties(prefix = DATA_SOURCE_CONFIG_PREFIX)
	public DataSourceConfig dataSourceConfig() {
		return new DataSourceConfig();
	}

	@Bean
	@ConfigurationProperties(prefix = PACKAGE_CONFIG_PREFIX)
	public PackageConfig packageConfig() {
		PackageConfig config = new PackageConfig();
		config.setEntity("dal.entity");
		config.setMapper("dal.mapper");
		config.setXml("dal.mapper");
		return config;
	}

	@Bean
	@ConfigurationProperties(prefix = TEMPLATE_CONFIG_PREFIX)
	public TemplateConfig templateConfig(BeforeConfig bc) {
		TemplateConfig config = bc.geTemplate();
		// 配置自定义输出模板
		// 指定自定义模板路径，注意不要带上.ftl/.vm, 会根据使用的模板引擎自动识别
		// templateConfig.setEntity("templates/entity2.java");
		// templateConfig.setService();
		// templateConfig.setController();
		// config.setXml(null);
		return config;
	}

	@Bean
	@ConfigurationProperties(prefix = STRATEGY_CONFIG_PREFIX)
	public StrategyConfig strategyConfig() {
		// 策略配置
		StrategyConfig strategy = new StrategyConfig();
		strategy.setNaming(NamingStrategy.underline_to_camel);
		strategy.setColumnNaming(NamingStrategy.underline_to_camel);
		strategy.setEntityLombokModel(true);
		strategy.setEntityBuilderModel(true);
		// strategy.setEntityColumnConstant(true);
		// strategy.setEntityTableFieldAnnotationEnable(true);
		// strategy.setSuperEntityClass("com.baomidou.ant.common.BaseEntity");
		// strategy.setSuperEntityColumns("id");
		// strategy.setSuperControllerClass("com.baomidou.ant.common.BaseController");
		// strategy.setRestControllerStyle(true);
		strategy.setControllerMappingHyphenStyle(true);
		strategy.setVersionFieldName("version");
		return strategy;
	}

	@Bean
	public InjectionConfig InjectionConfig() {
		// 自定义配置
		InjectionConfig cfg = new InjectionConfig() {
			@Override
			public void initMap() {
			}
		};
		// 自定义输出配置
		List<FileOutConfig> focList = new ArrayList<>();
		cfg.setFileOutConfigList(focList);
		return cfg;
	}

}
