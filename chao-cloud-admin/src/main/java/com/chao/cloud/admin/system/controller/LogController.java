package com.chao.cloud.admin.system.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.chao.cloud.admin.system.service.LogService;
import com.chao.cloud.admin.system.utils.Query;
import com.chao.cloud.admin.system.utils.R;

@RequestMapping("/sys/log")
@Controller
public class LogController {
    @Autowired
    LogService logService;
    String prefix = "system/log";

    @GetMapping()
    String log() {
        return prefix + "/log";
    }

    @ResponseBody
    @GetMapping("/list")
    R list(@RequestParam Map<String, Object> params) {
        Query query = new Query(params);
        return logService.queryList(query);
    }

    @ResponseBody
    @PostMapping("/remove")
    R remove(Long id) {
        if (logService.remove(id) > 0) {
            return R.ok();
        }
        return R.error();
    }

    @ResponseBody
    @PostMapping("/batchRemove")
    R batchRemove(@RequestParam("ids[]") Long[] ids) {
        int r = logService.batchRemove(ids);
        if (r > 0) {
            return R.ok();
        }
        return R.error();
    }
}
