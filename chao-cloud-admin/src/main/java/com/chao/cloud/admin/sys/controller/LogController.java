package com.chao.cloud.admin.sys.controller;

import java.util.ArrayList;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chao.cloud.admin.sys.dal.entity.SysLog;
import com.chao.cloud.admin.sys.service.SysLogService;
import com.chao.cloud.common.entity.Response;
import com.chao.cloud.common.entity.ResponseResult;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;

@RequestMapping("/sys/log")
@Controller
@Validated
public class LogController {

	@Autowired
	private SysLogService sysLogService;

	String prefix = "sys/log";

	@GetMapping()
	String log() {
		return prefix + "/log";
	}

	@ResponseBody
	@GetMapping("/list")
	Response<IPage<SysLog>> list(Page<SysLog> page, String username, String operation) {
		LambdaQueryWrapper<SysLog> queryWrapper = Wrappers.<SysLog>lambdaQuery();
		if (StrUtil.isNotBlank(username)) {
			queryWrapper.like(SysLog::getUsername, username);
		}
		if (StrUtil.isNotBlank(operation)) {
			queryWrapper.like(SysLog::getOperation, operation);
		}
		// 根据id降序
		queryWrapper.orderByDesc(SysLog::getId);
		return ResponseResult.getResponseResult(sysLogService.page(page, queryWrapper));
	}

	@ResponseBody
	@PostMapping("/remove")
	Response<String> remove(@NotNull Long id) {
		if (sysLogService.removeById(id)) {
			return ResponseResult.ok();
		}
		return ResponseResult.error();
	}

	@ResponseBody
	@PostMapping("/batchRemove")
	Response<String> batchRemove(@Size(min = 1) @RequestParam("ids[]") Long[] ids) {
		ArrayList<Long> idList = CollUtil.toList(ids);
		if (sysLogService.removeByIds(idList)) {
			return ResponseResult.ok();
		}
		return ResponseResult.error();
	}
}
