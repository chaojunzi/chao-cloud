package com.chao.cloud.common.extra.mybatis.annotation;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.incrementer.IKeyGenerator;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.incrementer.OracleKeyGenerator;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;

import cn.hutool.extra.spring.SpringUtil;
import lombok.Data;
import lombok.Getter;

@Data
@Configuration
@ConfigurationProperties(prefix = Constants.MYBATIS_PLUS)
public class MybatisPlusConfig {
	/**
	 * 数据源文件
	 */
	@Getter
	private static final List<String> dbFileList = new ArrayList<>();

	/**
	 * 数据源类型:默认mysql
	 */
	private DbType dbType = DbType.MYSQL;

	@Bean
	@ConditionalOnMissingBean(SpringUtil.class)
	SpringUtil springUtil() {
		return new SpringUtil();
	}

	/**
	 * mybatis-plus插件
	 * 
	 * @return {@link MybatisPlusInterceptor}
	 */
	@Bean
	public MybatisPlusInterceptor mybatisPlusInterceptor() {
		MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
		// 设置sql的limit为无限制，默认是500
		PaginationInnerInterceptor pageInterceptor = new PaginationInnerInterceptor();
		pageInterceptor.setMaxLimit(-1L);
		interceptor.addInnerInterceptor(pageInterceptor);
		// 乐观锁插件
		OptimisticLockerInnerInterceptor lockerInnerInterceptor = new OptimisticLockerInnerInterceptor();
		interceptor.addInnerInterceptor(lockerInnerInterceptor);
		return interceptor;
	}

	/**
	 * oracle 主键自增
	 * 
	 * @return {@link OracleKeyGenerator}
	 */
	@Bean
	@ConditionalOnProperty(prefix = Constants.MYBATIS_PLUS, name = "db-type", havingValue = "oracle")
	public IKeyGenerator keyGenerator() {
		return new OracleKeyGenerator();
	}

}
