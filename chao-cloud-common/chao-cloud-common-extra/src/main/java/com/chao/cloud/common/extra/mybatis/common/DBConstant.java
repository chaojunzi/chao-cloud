package com.chao.cloud.common.extra.mybatis.common;

import com.chao.cloud.common.util.ConfigUtil;

import cn.hutool.core.util.StrUtil;

/**
 * db的一些常量
 * 
 * @author 薛超
 * @since 2021年12月17日
 * @version 1.0.0
 */
public interface DBConstant {

	/**
	 * 配置前缀
	 */
	String DYNAMIC_CONFIG_PREFIX = "spring.datasource.dynamic";
	/**
	 * 默认数据库路径
	 */
	String DB_PATH = "./db";
	/**
	 * db后缀
	 */
	String DB_SUFFIX = ".db";

	/**
	 * 获取数据库路径
	 */
	static String getDBPath(String dbPath) {
		if (StrUtil.isBlank(dbPath)) {
			dbPath = DB_PATH;
		}
		return ConfigUtil.getAbsPath(dbPath);
	}
}
