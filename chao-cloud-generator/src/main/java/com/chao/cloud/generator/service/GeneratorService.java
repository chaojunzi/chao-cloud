package com.chao.cloud.generator.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import org.springframework.stereotype.Service;

import com.chao.cloud.common.exception.BusinessException;
import com.chao.cloud.generator.domain.dto.MysqlTableDTO;
import com.chao.cloud.generator.domain.vo.ConnVO;

import cn.hutool.core.util.StrUtil;

/**
 * 
 * @功能：代码生成
 * @author： 薛超
 * @时间：2019年5月20日
 * @version 2.0
 */
@Service
public interface GeneratorService {

	/**
	 * 查询表信息
	 * @param iDbQuery
	 * @return
	 */
	List<MysqlTableDTO> list(ConnVO vo);

	/**
	 * 连接地址
	 */
	String DATASOURCE_URL = "jdbc:mysql://{}:{}/{}?useSSL=false&useUnicode=true&characterEncoding=UTF8&allowMultiQueries=true&serverTimezone=GMT%2B8";
	/**
	 * 连接对象
	 */
	String DATASOURCE_DRIVER_CLASS_NAME = "com.mysql.cj.jdbc.Driver";
	/**
	 * 查询sql
	 */
	String TABLES_SQL = "show table status";

	/**
	 * 创建数据库连接对象
	 *
	 * @return Connection
	 */
	static Connection getConn(String url, String username, String password) {
		try {
			Class.forName(DATASOURCE_DRIVER_CLASS_NAME);
			return DriverManager.getConnection(url, username, password);
		} catch (ClassNotFoundException | SQLException e) {
			throw new BusinessException(
					StrUtil.format("连接创建失败:[url={},username={},password={}]", url, username, password));
		}
	}
}
