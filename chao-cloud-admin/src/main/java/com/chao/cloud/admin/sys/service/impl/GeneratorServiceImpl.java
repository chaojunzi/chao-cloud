package com.chao.cloud.admin.sys.service.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.IDbQuery;
import com.chao.cloud.admin.sys.domain.dto.MysqlTableDTO;
import com.chao.cloud.admin.sys.service.GeneratorService;
import com.chao.cloud.common.exception.BusinessException;

import cn.hutool.core.util.StrUtil;
import cn.hutool.db.handler.BeanListHandler;
import cn.hutool.db.sql.SqlExecutor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class GeneratorServiceImpl implements GeneratorService {

	@Autowired
	private DataSourceConfig config;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public List<MysqlTableDTO> list(String tableName) {
		IDbQuery dbQuery = config.getDbQuery();
		if (dbQuery.dbType() != DbType.MYSQL) {
			return Collections.emptyList();
		}
		String tablesSql = dbQuery.tablesSql();
		if (StrUtil.isNotBlank(tableName)) {
			tablesSql = StrUtil.format(tablesSql + " LIKE '%{}%'", tableName);
		}
		log.info("url={}", config.getUrl());
		log.info("tablesSql={}", tablesSql);
		try (Connection conn = config.getConn()) {
			return (List<MysqlTableDTO>) SqlExecutor.query(conn, tablesSql, new BeanListHandler(MysqlTableDTO.class));
		} catch (SQLException e) {
			throw new BusinessException(e.getMessage());
		}
	}
}
