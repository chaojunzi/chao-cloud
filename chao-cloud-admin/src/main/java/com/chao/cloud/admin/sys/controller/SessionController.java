package com.chao.cloud.admin.sys.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chao.cloud.admin.sys.log.AdminLog;
import com.chao.cloud.admin.sys.service.SessionService;
import com.chao.cloud.admin.sys.shiro.ShiroUserOnline;
import com.chao.cloud.common.entity.Response;
import com.chao.cloud.common.extra.mybatis.generator.menu.MenuEnum;
import com.chao.cloud.common.extra.mybatis.generator.menu.MenuMapping;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;

@RequestMapping("/sys/online")
@Controller
@MenuMapping
public class SessionController extends BaseController {
	@Autowired
	SessionService sessionService;

	@RequestMapping
	@MenuMapping(value = "在线用户", type = MenuEnum.MENU)
	@RequiresPermissions("sys:online:list")
	public String list(Model model) {
		model.addAttribute("username", getUser().getUsername());
		return "sys/online/online";
	}

	@AdminLog(AdminLog.STAT_PREFIX + "在线用户列表")
	@MenuMapping("列表")
	@RequiresPermissions("sys:online:list")
	@RequestMapping("/list")
	@ResponseBody
	public Response<IPage<ShiroUserOnline>> list(String username) {
		Page<ShiroUserOnline> result = new Page<>();
		List<ShiroUserOnline> list = sessionService.list();
		// 过滤
		if (CollUtil.isNotEmpty(list) && StrUtil.isNotBlank(username)) {
			list = list.stream().filter(l -> StrUtil.containsIgnoreCase(l.getUsername(), username))
					.collect(Collectors.toList());
		}
		result.setRecords(list);
		return Response.ok(result);
	}

	@MenuMapping("强制下线")
	@RequiresPermissions("sys:online:forceLogout")
	@RequestMapping("/forceLogout/{sessionId}")
	@ResponseBody
	public Response<String> forceLogout(@PathVariable("sessionId") String sessionId) {
		sessionService.forceLogout(sessionId);
		return Response.ok();

	}

}
