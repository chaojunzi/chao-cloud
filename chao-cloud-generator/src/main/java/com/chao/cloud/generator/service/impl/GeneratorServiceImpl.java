package com.chao.cloud.generator.service.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.chao.cloud.common.exception.BusinessException;
import com.chao.cloud.generator.dal.entity.XcConfig;
import com.chao.cloud.generator.dal.mapper.XcConfigMapper;
import com.chao.cloud.generator.domain.dto.MysqlTableDTO;
import com.chao.cloud.generator.domain.vo.ConnVO;
import com.chao.cloud.generator.service.GeneratorService;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.handler.BeanListHandler;
import cn.hutool.db.sql.SqlExecutor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class GeneratorServiceImpl implements GeneratorService {

	@Autowired
	private XcConfigMapper xcConfigMapper;

	@Override
	public List<MysqlTableDTO> list(ConnVO vo) {
		String tablesSql = GeneratorService.TABLES_SQL;
		if (StrUtil.isNotBlank(vo.getTableName())) {
			tablesSql = StrUtil.format(tablesSql + " LIKE '%{}%'", vo.getTableName());
		}
		log.info("tablesSql={}", tablesSql);
		XcConfig config = xcConfigMapper.selectById(vo.getId());
		Assert.notNull(config, "config id={} 无效", vo.getId());
		String url = StrUtil.format(GeneratorService.DATASOURCE_URL, config.getHost(), config.getPort(),
				config.getDatabase());
		log.info("url={}", url);
		try (Connection conn = GeneratorService.getConn(url, config.getUsername(), config.getPassword())) {
			return (List<MysqlTableDTO>) SqlExecutor.query(conn, tablesSql, new BeanListHandler(MysqlTableDTO.class));
		} catch (SQLException e) {
			throw new BusinessException(e.getMessage());
		}
	}
}
