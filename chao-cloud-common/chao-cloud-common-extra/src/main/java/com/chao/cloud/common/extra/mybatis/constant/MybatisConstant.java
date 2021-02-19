package com.chao.cloud.common.extra.mybatis.constant;

/**
 * mybatis 相关常量
 * 
 * @author 薛超
 * @since 2020年7月10日
 * @version 1.0.9
 */
public interface MybatisConstant {
	/**
	 * 只查询一条 limit 1
	 */
	String LIMIT_1 = "LIMIT 1";
	String LIMIT_1_ORACLE = "ROWNUM = 1";
	/**
	 * 第一页
	 */
	int CURRENT_PAGE = 1;
	/**
	 * 默认size 为1000
	 */
	Integer SIZE = 1000;
}
