package com.chao.cloud.admin.system.controller;

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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.chao.cloud.admin.system.service.SessionService;
import com.chao.cloud.admin.system.shiro.ShiroUserOnline;
import com.chao.cloud.admin.system.utils.R;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.StaticLog;

@RequestMapping("/sys/online")
@Controller
public class SessionController {
    @Autowired
    SessionService sessionService;

    @GetMapping()
    public String online() {
        return "system/online/online";
    }

    @ResponseBody
    @RequestMapping("/list")
    public R list(String username) {
        List<ShiroUserOnline> list = sessionService.list();
        // 过滤
        if (CollUtil.isNotEmpty(list) && StrUtil.isNotBlank(username)) {
            list = list.stream().filter(l -> StrUtil.containsIgnoreCase(l.getUsername(), username))
                    .collect(Collectors.toList());
        }
        return R.page(list);
    }

    @ResponseBody
    @RequestMapping("/forceLogout/{sessionId}")
    public R forceLogout(@PathVariable("sessionId") String sessionId, RedirectAttributes redirectAttributes) {
        try {
            sessionService.forceLogout(sessionId);
            return R.ok();
        } catch (Exception e) {
            StaticLog.info("session-error->{}", e);
            return R.error();
        }

    }

    @ResponseBody
    @RequestMapping("/sessionList")
    public Collection<Session> sessionList() {
        return sessionService.sessionList();
    }

}
