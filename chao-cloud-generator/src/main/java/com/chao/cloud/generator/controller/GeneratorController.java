package com.chao.cloud.generator.controller;

import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.chao.cloud.common.entity.Response;
import com.chao.cloud.common.extra.mybatis.generator.ZipAutoGenerator;
import com.chao.cloud.common.extra.mybatis.generator.template.BeforeConfig;
import com.chao.cloud.generator.dal.entity.XcConfig;
import com.chao.cloud.generator.domain.dto.MysqlTableDTO;
import com.chao.cloud.generator.domain.vo.ConnVO;
import com.chao.cloud.generator.domain.vo.GenCodeVO;
import com.chao.cloud.generator.service.GeneratorService;
import com.chao.cloud.generator.service.XcConfigService;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.StaticLog;

/**
 * 代码生成
 * @功能：
 * @author： 薛超
 * @时间： 2019年7月24日
 * @version 1.0.2
 */
@RequestMapping("generator")
@Controller
@Validated
public class GeneratorController {
	String prefix = "generator";
	@Autowired
	private GeneratorService generatorService;
	@Autowired
	private ZipAutoGenerator autoGenerator;
	@Autowired
	private BeforeConfig beforeConfig;
	@Autowired
	private XcConfigService xcConfigService;

	private Lock lock = new ReentrantLock();

	@RequestMapping
	String generator(@NotNull Integer id, Model m) {
		XcConfig config = xcConfigService.getById(id);
		m.addAttribute("config", config);
		return prefix + "/list";
	}

	@RequestMapping("/list")
	@ResponseBody
	Response<IPage<MysqlTableDTO>> list(@Valid ConnVO vo, Page<MysqlTableDTO> page) {
		List<MysqlTableDTO> list = generatorService.list(vo);
		page.setTotal(list.size());
		// 分页
		if (CollUtil.isNotEmpty(list)) {
			// 判断总页数
			long pages = page.getPages();
			if (pages > 0 && page.getCurrent() <= pages) {// 当前页不超过总页数
				// 获取偏移量
				long offset = page.offset();
				list = list.stream().skip(offset).limit(page.getSize()).collect(Collectors.toList());
				page.setRecords(list);
			}
		}
		return Response.ok(page);
	}

	@RequestMapping("/code/{tableName}")
	public void code(HttpServletResponse response, @Valid GenCodeVO.Extra vo,
			@PathVariable("tableName") String tableName) throws Exception {
		this.genCode(response, vo, tableName);
	}

	@RequestMapping("/batchCode")
	public void batchCode(HttpServletResponse response, @Valid GenCodeVO.Extra vo,
			@NotEmpty @RequestParam String tables[]) throws Exception {
		this.genCode(response, vo, tables);
	}

	@RequestMapping("/test")
	@ResponseBody
	Response<String> test(@Valid GenCodeVO.Conn vo) throws SQLException {
		// 测试是否连接成功
		String url = StrUtil.format(GeneratorService.DATASOURCE_URL, vo.getHost(), vo.getPort(), vo.getDatabase());
		try (Connection conn = GeneratorService.getConn(url, vo.getUsername(), vo.getPassword());) {
		} catch (Exception e) {
			StaticLog.error(e.getMessage());
			return Response.error(StrUtil.format("<br/>连接失败:[{}:{}?{}->{}:{}]", vo.getHost(), vo.getPort(),
					vo.getDatabase(), vo.getUsername(), vo.getPassword()));
		}
		return Response.ok();
	}

	private void genCode(HttpServletResponse response, GenCodeVO.Extra vo, String... tableName) throws Exception {
		// 生成代码
		lock.lock();
		try (OutputStream out = response.getOutputStream()) {
			XcConfig xcConfig = xcConfigService.getById(vo.getId());
			Assert.notNull(xcConfig, "无效的连接[id={}]", vo.getId());
			// 配置连接
			DataSourceConfig config = autoGenerator.getDataSource();
			String url = StrUtil.format(GeneratorService.DATASOURCE_URL, xcConfig.getHost(), xcConfig.getPort(),
					xcConfig.getDatabase());
			config.setUrl(url);
			config.setUsername(xcConfig.getUsername());
			config.setPassword(xcConfig.getPassword());
			autoGenerator.getPackageInfo().setParent(vo.getPackageName());
			autoGenerator.getGlobalConfig().setAuthor(vo.getAuthor());
			autoGenerator.getStrategy().setInclude(tableName);
			beforeConfig.setTemplateStyle(vo.getStyle());
			autoGenerator.setTemplate(beforeConfig.getTemplate());
			// 生成
			response.reset();
			response.setHeader("Content-Disposition", "attachment; filename=\"" + tableName[0] + "-more.zip\"");
			response.setContentType("application/octet-stream; charset=UTF-8");
			autoGenerator.execute(out);
		} finally {
			lock.unlock();
		}
	}

}
