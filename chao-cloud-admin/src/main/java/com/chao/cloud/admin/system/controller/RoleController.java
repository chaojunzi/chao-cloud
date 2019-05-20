package com.chao.cloud.admin.system.controller;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.chao.cloud.admin.system.annotation.AdminLog;
import com.chao.cloud.admin.system.domain.dto.RoleDTO;
import com.chao.cloud.admin.system.service.RoleService;
import com.chao.cloud.admin.system.utils.Query;
import com.chao.cloud.admin.system.utils.R;

@RequestMapping("/sys/role")
@Controller
public class RoleController extends BaseController {
    String prefix = "system/role";
    @Autowired
    RoleService roleService;

    @RequiresPermissions("sys:role:role")
    @GetMapping()
    String role() {
        return prefix + "/role";
    }

    @AdminLog(AdminLog.STAT_PREFIX + "权限列表")
    @RequiresPermissions("sys:role:role")
    @GetMapping("/list")
    @ResponseBody
    R list(@RequestParam Map<String, Object> params) {
        Query query = new Query(params);
        int count = roleService.count(params);
        if (count < 1) {
            return R.page(Collections.emptyList(), count);
        }
        List<RoleDTO> roles = roleService.list(query);
        return R.page(roles, count);
    }

    @AdminLog("添加角色")
    @RequiresPermissions("sys:role:add")
    @GetMapping("/add")
    String add() {
        return prefix + "/add";
    }

    @AdminLog("编辑角色")
    @RequiresPermissions("sys:role:edit")
    @GetMapping("/edit/{id}")
    String edit(@PathVariable("id") Long id, Model model) {
        RoleDTO roleDO = roleService.get(id);
        model.addAttribute("role", roleDO);
        return prefix + "/edit";
    }

    @AdminLog("保存角色")
    @RequiresPermissions("sys:role:add")
    @PostMapping("/save")
    @ResponseBody
    R save(RoleDTO role) {
        if (roleService.save(role) > 0) {
            return R.ok();
        } else {
            return R.error(1, "保存失败");
        }
    }

    @AdminLog("更新角色")
    @RequiresPermissions("sys:role:edit")
    @PostMapping("/update")
    @ResponseBody
    R update(RoleDTO role) {
        if (roleService.update(role) > 0) {
            return R.ok();
        } else {
            return R.error(1, "保存失败");
        }
    }

    @AdminLog("删除角色")
    @RequiresPermissions("sys:role:remove")
    @PostMapping("/remove")
    @ResponseBody
    R save(Long id) {
        if (roleService.remove(id) > 0) {
            return R.ok();
        } else {
            return R.error(1, "删除失败");
        }
    }

    @RequiresPermissions("sys:role:batchRemove")
    @AdminLog("批量删除角色")
    @PostMapping("/batchRemove")
    @ResponseBody
    R batchRemove(@RequestParam("ids[]") Long[] ids) {
        int r = roleService.batchremove(ids);
        if (r > 0) {
            return R.ok();
        }
        return R.error();
    }
}
