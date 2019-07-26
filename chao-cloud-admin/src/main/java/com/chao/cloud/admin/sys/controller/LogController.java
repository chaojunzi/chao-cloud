package com.chao.cloud.admin.sys.controller;

import java.util.ArrayList;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
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
import com.chao.cloud.common.extra.mybatis.generator.menu.MenuEnum;
import com.chao.cloud.common.extra.mybatis.generator.menu.MenuMapping;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;

@RequestMapping("/sys/log")
@Controller
@Validated
@MenuMapping
public class LogController {

	@Autowired
	private SysLogService sysLogService;

	String prefix = "sys/log";

	@MenuMapping(value = "系统日志", type = MenuEnum.MENU)
	@RequiresPermissions("sys:log:list")
	@RequestMapping
	String log() {
		return prefix + "/log";
	}

	@MenuMapping("列表")
	@RequiresPermissions("sys:log:list")
	@RequestMapping("/list")
	@ResponseBody
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
		return Response.ok(sysLogService.page(page, queryWrapper));
	}

	@MenuMapping("删除")
	@RequiresPermissions("sys:log:remove")
	@RequestMapping("/remove")
	@ResponseBody
	Response<String> remove(@NotNull Long id) {
		if (sysLogService.removeById(id)) {
			return Response.ok();
		}
		return Response.error();
	}

	@MenuMapping("批量删除")
	@RequiresPermissions("sys:log:batchRemove")
	@RequestMapping("/batchRemove")
	@ResponseBody
	Response<String> batchRemove(@Size(min = 1) @RequestParam("ids[]") Long[] ids) {
		ArrayList<Long> idList = CollUtil.toList(ids);
		if (sysLogService.removeByIds(idList)) {
			return Response.ok();
		}
		return Response.error();
	}
}
