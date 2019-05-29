package com.chao.cloud.admin.sys.controller;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chao.cloud.admin.sys.log.AdminLog;
import com.chao.cloud.admin.sys.service.SessionService;
import com.chao.cloud.admin.sys.shiro.ShiroUserOnline;
import com.chao.cloud.common.entity.Response;
import com.chao.cloud.common.entity.ResponseResult;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;

@RequestMapping("/sys/online")
@Controller
public class SessionController {
	@Autowired
	SessionService sessionService; 

	@GetMapping()
	public String online() {
		return "sys/online/online";
	}

	@ResponseBody
	@AdminLog(AdminLog.STAT_PREFIX + "在线用户列表")
	@RequestMapping("/list")
	public Response<IPage<ShiroUserOnline>> list(String username) {
		Page<ShiroUserOnline> result = new Page<>();
		List<ShiroUserOnline> list = sessionService.list();
		// 过滤
		if (CollUtil.isNotEmpty(list) && StrUtil.isNotBlank(username)) {
			list = list.stream().filter(l -> StrUtil.containsIgnoreCase(l.getUsername(), username))
					.collect(Collectors.toList());
		}
		result.setRecords(list);
		return ResponseResult.getResponseResult(result);
	}

	@ResponseBody
	@RequestMapping("/forceLogout/{sessionId}")
	public Response<String> forceLogout(@PathVariable("sessionId") String sessionId) {
		sessionService.forceLogout(sessionId);
		return ResponseResult.ok();

	}

	@ResponseBody
	@RequestMapping("/sessionList")
	public Collection<Session> sessionList() {
		return sessionService.sessionList();
	}

}
