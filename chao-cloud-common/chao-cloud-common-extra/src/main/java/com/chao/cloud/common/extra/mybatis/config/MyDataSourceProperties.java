package com.chao.cloud.common.extra.mybatis.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.chao.cloud.common.extra.mybatis.common.DBConstant;

import lombok.Data;

/**
 * 数据源相关配置
 * 
 * @author 薛超
 * @since 2022年5月23日
 * @version 1.0.0
 */
@Data
@ConfigurationProperties(prefix = DBConstant.DS_CONFIG_PREFIX)
public class MyDataSourceProperties {

	/**
	 * 字段压缩存储
	 */
	private boolean columnCompress = true;
	/**
	 * insert之前获取主键自增<br>
	 * true:默认mybatis-plus支持<br>
	 * false:仅针对idType=input 生效
	 */
	private boolean selectKeyBefore = true;

}
