package com.chao.cloud.admin.system.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.chao.cloud.admin.system.annotation.AdminLog;
import com.chao.cloud.admin.system.service.GeneratorService;
import com.chao.cloud.admin.system.utils.GenUtils;
import com.chao.cloud.admin.system.utils.R;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;

@RequestMapping("/sys/generator")
@Controller
public class GeneratorController {
    String prefix = "system/generator";

    @Autowired
    GeneratorService generatorService;

    @GetMapping()
    String generator() {
        return prefix + "/list";
    }

    @ResponseBody
    @AdminLog(AdminLog.STAT_PREFIX + "数据表")
    @GetMapping("/list")
    R list(String tableName) {
        List<Map<String, Object>> list = generatorService.list();
        if (CollUtil.isNotEmpty(list) && StrUtil.isNotBlank(tableName)) {
            list = list.stream().filter(l -> StrUtil.containsIgnoreCase(l.get("tableName").toString(), tableName))
                    .collect(Collectors.toList());
        }
        return R.page(list);
    }

    @RequestMapping("/code/{tableName}")
    public void code(HttpServletResponse response, @PathVariable("tableName") String tableName) throws IOException {
        String[] tableNames = new String[] { tableName };
        genCode(response, tableNames);
    }

    @RequestMapping("/batchCode")
    public void batchCode(HttpServletResponse response, String tables) throws IOException {
        String[] tableNames = new String[] {};
        tableNames = JSON.parseArray(tables).toArray(tableNames);
        genCode(response, tableNames);
    }

    @GetMapping("/edit")
    public String edit(Model model) {
        Configuration conf = GenUtils.getConfig();
        Map<String, Object> property = new HashMap<>(16);
        property.put("author", conf.getProperty("author"));
        property.put("email", conf.getProperty("email"));
        property.put("package", conf.getProperty("package"));
        property.put("autoRemovePre", conf.getProperty("autoRemovePre"));
        property.put("tablePrefix", conf.getProperty("tablePrefix"));
        model.addAttribute("property", property);
        return prefix + "/edit";
    }

    @ResponseBody
    @PostMapping("/update")
    R update(@RequestParam Map<String, Object> map) {
        try {
            PropertiesConfiguration conf = GenUtils.getConfig();
            conf.setProperty("author", map.get("author"));
            conf.setProperty("email", map.get("email"));
            conf.setProperty("package", map.get("package"));
            conf.setProperty("autoRemovePre", map.get("autoRemovePre"));
            conf.setProperty("tablePrefix", map.get("tablePrefix"));
            conf.save();
        } catch (ConfigurationException e) {
            return R.error("保存配置文件出错");
        }
        return R.ok();
    }

    private void genCode(HttpServletResponse response, String[] tableNames) throws IOException {
        byte[] data = generatorService.generatorCode(tableNames);
        response.reset();
        response.setHeader("Content-Disposition", "attachment; filename=\"" + tableNames[0] + "-more.zip\"");
        response.addHeader("Content-Length", "" + data.length);
        response.setContentType("application/octet-stream; charset=UTF-8");
        IoUtil.write(response.getOutputStream(), true, data);
    }

}
