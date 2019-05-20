package com.chao.cloud.admin.system.controller;

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
import com.chao.cloud.admin.system.domain.dto.MenuDTO;
import com.chao.cloud.admin.system.domain.dto.MenuLayuiDTO;
import com.chao.cloud.admin.system.domain.dto.TreeDTO;
import com.chao.cloud.admin.system.service.MenuService;
import com.chao.cloud.admin.system.utils.R;

/**
 * 
 * @功能：
 * @author： 薛超
 * @时间：2019年5月8日
 * @version 2.0
 */
@RequestMapping("/sys/menu")
@Controller
public class MenuController extends BaseController {
    String prefix = "system/menu";
    @Autowired
    MenuService menuService;

    @RequiresPermissions("sys:menu:menu")
    @GetMapping()
    String menu(Model model) {
        return prefix + "/menu";
    }

    @AdminLog(AdminLog.STAT_PREFIX + "菜单列表")
    @RequiresPermissions("sys:menu:menu")
    @RequestMapping("/list")
    @ResponseBody
    R list(@RequestParam Map<String, Object> params) {
        List<MenuDTO> list = menuService.list(params);
        return R.page(list);
    }

    @AdminLog("添加菜单")
    @RequiresPermissions("sys:menu:add")
    @GetMapping("/add/{pId}")
    String add(Model model, @PathVariable("pId") Long pId) {
        model.addAttribute("pId", pId);
        if (pId == 0) {
            model.addAttribute("pName", "根目录");
        } else {
            model.addAttribute("pName", menuService.get(pId).getName());
        }
        return prefix + "/add";
    }

    @AdminLog("编辑菜单")
    @RequiresPermissions("sys:menu:edit")
    @GetMapping("/edit/{id}")
    String edit(Model model, @PathVariable("id") Long id) {
        MenuDTO mdo = menuService.get(id);
        Long pId = mdo.getParentId();
        model.addAttribute("pId", pId);
        if (pId == 0) {
            model.addAttribute("pName", "根目录");
        } else {
            model.addAttribute("pName", menuService.get(pId).getName());
        }
        model.addAttribute("menu", mdo);
        return prefix + "/edit";
    }

    @AdminLog("保存菜单")
    @RequiresPermissions("sys:menu:add")
    @PostMapping("/save")
    @ResponseBody
    R save(MenuDTO menu) {
        if (menuService.save(menu) > 0) {
            return R.ok();
        } else {
            return R.error(1, "保存失败");
        }
    }

    @AdminLog("更新菜单")
    @RequiresPermissions("sys:menu:edit")
    @PostMapping("/update")
    @ResponseBody
    R update(MenuDTO menu) {
        if (menuService.update(menu) > 0) {
            return R.ok();
        } else {
            return R.error(1, "更新失败");
        }
    }

    @AdminLog("删除菜单")
    @RequiresPermissions("sys:menu:remove")
    @PostMapping("/remove")
    @ResponseBody
    R remove(Long id) {
        if (menuService.remove(id) > 0) {
            return R.ok();
        } else {
            return R.error(1, "删除失败");
        }
    }

    @GetMapping("/tree")
    @ResponseBody
    TreeDTO<MenuDTO> tree() {
        TreeDTO<MenuDTO> tree = menuService.getTree();
        return tree;
    }

    @GetMapping("/userList")
    @ResponseBody
    List<MenuLayuiDTO> userList() {
        return menuService.listMenuLayuiTree(getUserId());
    }

}
