package com.chao.cloud.im.code.core;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.IDbQuery;
import com.chao.cloud.common.exception.BusinessException;
import com.chao.cloud.im.code.dto.H2TableDTO;
import com.chao.cloud.im.code.dto.MysqlTableDTO;

import cn.hutool.core.collection.CollUtil;
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
	public List<IDatabaseInfo> list(String tableName) {
		IDbQuery dbQuery = config.getDbQuery();
		log.info("url={}", config.getUrl());

		switch (dbQuery.dbType()) {
		case MYSQL:
			String tablesSql = dbQuery.tablesSql();
			if (StrUtil.isNotBlank(tableName)) {
				tablesSql = StrUtil.format(tablesSql + " LIKE '%{}%'", tableName);
			}
			log.info("tablesSql={}", tablesSql);
			try (Connection conn = config.getConn()) {
				return (List<IDatabaseInfo>) SqlExecutor.query(conn, tablesSql,
						new BeanListHandler(MysqlTableDTO.class));
			} catch (SQLException e) {
				throw new BusinessException(e.getMessage());
			}
		case H2:
			String h2TablesSql = "SHOW TABLES";
			log.info("tablesSql={}", h2TablesSql);
			try (Connection conn = config.getConn()) {
				List<IDatabaseInfo> list = (List<IDatabaseInfo>) SqlExecutor.query(conn, h2TablesSql,
						new BeanListHandler(H2TableDTO.class));
				// 过滤
				if (StrUtil.isNotBlank(tableName) && CollUtil.isNotEmpty(list)) {
					list = list.stream().filter(l -> l.getTableName().contains(tableName)).collect(Collectors.toList());
				}
				return list;
			} catch (SQLException e) {
				throw new BusinessException(e.getMessage());
			}
		default:
			return Collections.emptyList();
		}
	}
}
