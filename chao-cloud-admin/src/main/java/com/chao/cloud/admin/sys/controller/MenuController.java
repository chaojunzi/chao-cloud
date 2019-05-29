package com.chao.cloud.admin.sys.controller;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.extension.api.R;
import com.chao.cloud.admin.sys.dal.entity.SysMenu;
import com.chao.cloud.admin.sys.domain.dto.MenuLayuiDTO;
import com.chao.cloud.admin.sys.log.AdminLog;
import com.chao.cloud.admin.sys.service.SysMenuService;
import com.chao.cloud.common.entity.Response;
import com.chao.cloud.common.entity.ResponseResult;
import com.chao.cloud.common.exception.BusinessException;

/**
 * 
 * @功能：
 * @author： 薛超
 * @时间：2019年5月8日
 * @version 2.0
 */
@RequestMapping("/sys/menu")
@Controller
@Validated
public class MenuController extends BaseController {
	String prefix = "sys/menu";
	@Autowired
	private SysMenuService sysMenuService;

	@GetMapping
	@RequiresPermissions("sys:menu:menu")
	String menu(Model model) {
		return prefix + "/menu";
	}

	@AdminLog(AdminLog.STAT_PREFIX + "菜单列表")
	@RequiresPermissions("sys:menu:menu")
	@RequestMapping("/list")
	@ResponseBody
	R<List<SysMenu>> list() {
		return R.ok(sysMenuService.list());
	}

	@AdminLog("添加菜单")
	@RequiresPermissions("sys:menu:add")
	@GetMapping("/add/{pId}")
	String add(Model model, @PathVariable("pId") Long pId) {
		model.addAttribute("pId", pId);
		if (pId == 0) {
			model.addAttribute("pName", "根目录");
		} else {
			model.addAttribute("pName", sysMenuService.getById(pId).getName());
		}
		return prefix + "/add";
	}

	@AdminLog("编辑菜单")
	@RequiresPermissions("sys:menu:edit")
	@GetMapping("/edit/{id}")
	String edit(Model model, @PathVariable("id") Long id) {
		SysMenu mdo = sysMenuService.getById(id);
		Long pId = mdo.getParentId();
		model.addAttribute("pId", pId);
		if (pId == 0) {
			model.addAttribute("pName", "根目录");
		} else {
			model.addAttribute("pName", sysMenuService.getById(id).getName());
		}
		model.addAttribute("menu", mdo);
		return prefix + "/edit";
	}

	@AdminLog("保存菜单")
	@RequiresPermissions("sys:menu:add")
	@PostMapping("/save")
	@ResponseBody
	Response<String> save(SysMenu menu) {
		if (sysMenuService.save(menu)) {
			return ResponseResult.ok();
		}
		throw new BusinessException("保存失败");

	}

	@AdminLog("更新菜单")
	@RequiresPermissions("sys:menu:edit")
	@PostMapping("/update")
	@ResponseBody
	Response<String> update(SysMenu menu) {
		if (sysMenuService.updateById(menu)) {
			return ResponseResult.ok();
		}
		throw new BusinessException("更新失败");
	}

	@AdminLog("删除菜单")
	@RequiresPermissions("sys:menu:remove")
	@PostMapping("/remove")
	@ResponseBody
	Response<String> remove(@NotNull Long id) {
		if (sysMenuService.removeById(id)) {
			return ResponseResult.ok();
		}
		throw new BusinessException("删除失败");
	}

	@GetMapping("/leftList")
	@ResponseBody
	@RequiresUser
	List<MenuLayuiDTO> userList() {
		return sysMenuService.listMenuLayuiTree(getUserId());
	}

}
