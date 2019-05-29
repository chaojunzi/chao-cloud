package com.chao.cloud.admin.sys.controller;

import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.chao.cloud.admin.sys.domain.vo.GenCodeVO;
import com.chao.cloud.admin.sys.log.AdminLog;
import com.chao.cloud.admin.sys.service.GeneratorService;
import com.chao.cloud.common.entity.Response;
import com.chao.cloud.common.entity.ResponseResult;
import com.chao.cloud.common.extra.mybatis.generator.ZipAutoGenerator;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;

@RequestMapping("/sys/generator")
@Controller
@Validated
public class GeneratorController {
	String prefix = "sys/generator";

	@Autowired
	private GeneratorService generatorService;
	@Autowired
	private ZipAutoGenerator autoGenerator;

	@GetMapping()
	String generator() {
		return prefix + "/list";
	}

	@ResponseBody
	@AdminLog(AdminLog.STAT_PREFIX + "数据表")
	@GetMapping("/list")
	Response<IPage<Map<String, Object>>> list(String tableName) {
		Page<Map<String, Object>> result = new Page<>();
		List<Map<String, Object>> list = generatorService.list();
		if (CollUtil.isNotEmpty(list) && StrUtil.isNotBlank(tableName)) {
			list = list.stream().filter(l -> StrUtil.containsIgnoreCase(l.get("tableName").toString(), tableName))
					.collect(Collectors.toList());
		}
		result.setRecords(list);
		return ResponseResult.getResponseResult(result);
	}

	@RequestMapping("/code/{tableName}")
	public void code(HttpServletResponse response, @PathVariable("tableName") String tableName) throws Exception {
		autoGenerator.getStrategy().setInclude(tableName);
		genCode(tableName, response);
	}

	@RequestMapping("/batchCode")
	public void batchCode(HttpServletResponse response, @NotEmpty @RequestParam String tables[]) throws Exception {
		autoGenerator.getStrategy().setInclude(tables);
		genCode(tables[0], response);
	}

	@GetMapping("/edit")
	public String edit(Model model) {
		// 分块
		DataSourceConfig config = autoGenerator.getDataSource();
		GenCodeVO.Base base = new GenCodeVO.Base();
		base.setAuthor(autoGenerator.getGlobalConfig().getAuthor());
		base.setParent(autoGenerator.getPackageInfo().getParent());
		base.setUrl(config.getUrl());
		base.setPassword(config.getPassword());
		base.setUsername(config.getUsername());
		base.setDriverName(config.getDriverName());
		model.addAttribute("config", base);
		return prefix + "/edit";
	}

	@ResponseBody
	@PostMapping("/update")
	Response<String> update(@Valid GenCodeVO.Base vo) {
		DataSourceConfig config = autoGenerator.getDataSource();
		BeanUtil.copyProperties(vo, config);
		autoGenerator.getPackageInfo().setParent(vo.getParent());
		autoGenerator.getGlobalConfig().setAuthor(vo.getAuthor());
		return ResponseResult.ok();
	}

	private void genCode(String tableName, HttpServletResponse response) throws Exception {
		// 生成代码
		response.reset();
		response.setHeader("Content-Disposition", "attachment; filename=\"" + tableName + "-more.zip\"");
		response.setContentType("application/octet-stream; charset=UTF-8");
		try (OutputStream out = response.getOutputStream()) {
			autoGenerator.execute(out);
		}
	}

}
