package com.chao.cloud.admin.sys.controller;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chao.cloud.admin.sys.dal.entity.SysRole;
import com.chao.cloud.admin.sys.domain.dto.RoleDTO;
import com.chao.cloud.admin.sys.log.AdminLog;
import com.chao.cloud.admin.sys.service.SysRoleService;
import com.chao.cloud.common.entity.Response;
import com.chao.cloud.common.exception.BusinessException;
import com.chao.cloud.common.extra.mybatis.generator.menu.MenuEnum;
import com.chao.cloud.common.extra.mybatis.generator.menu.MenuMapping;

@RequestMapping("/sys/role")
@Controller
@Validated
@MenuMapping
public class RoleController extends BaseController {
	String prefix = "sys/role";
	@Autowired
	private SysRoleService sysRoleService;

	@MenuMapping(value = "角色管理", type = MenuEnum.MENU)
	@RequiresPermissions("sys:role:list")
	@RequestMapping
	String role() {
		return prefix + "/role";
	}

	@AdminLog(AdminLog.STAT_PREFIX + "权限列表")
	@MenuMapping("列表")
	@RequiresPermissions("sys:role:list")
	@RequestMapping("/list")
	@ResponseBody
	Response<IPage<SysRole>> list(Page<SysRole> page) {
		return Response.ok(sysRoleService.page(page));
	}

	@MenuMapping("增加")
	@RequiresPermissions("sys:role:add")
	@RequestMapping("/add")
	String add() {
		return prefix + "/add";
	}

	@MenuMapping("编辑")
	@RequiresPermissions("sys:role:edit")
	@RequestMapping("/edit/{id}")
	String edit(@PathVariable("id") Integer id, Model model) {
		RoleDTO roleDO = sysRoleService.get(id);
		model.addAttribute("role", roleDO);
		return prefix + "/edit";
	}

	@AdminLog("保存角色")
	@RequiresPermissions("sys:role:add")
	@RequestMapping("/save")
	@ResponseBody
	Response<String> save(RoleDTO role) {
		role.setMenuIds(super.removeEmpty(role.getMenuIds()));
		if (sysRoleService.save(role)) {
			return Response.ok();
		}
		throw new BusinessException("保存失败 ");
	}

	@AdminLog("更新角色")
	@RequiresPermissions("sys:role:edit")
	@RequestMapping("/update")
	@ResponseBody
	Response<String> update(RoleDTO role) {
		// 去重//去空
		role.setMenuIds(super.removeEmpty(role.getMenuIds()));
		if (sysRoleService.update(role)) {
			return Response.ok();
		}
		throw new BusinessException("更新失败 ");
	}

	@AdminLog("删除角色")
	@MenuMapping("删除")
	@RequiresPermissions("sys:role:remove")
	@RequestMapping("/remove")
	@ResponseBody
	Response<String> save(@NotNull Integer id) {
		if (sysRoleService.remove(id)) {
			return Response.ok();
		}
		throw new BusinessException("删除失败");

	}

	@AdminLog("批量删除角色")
	@MenuMapping("批量删除")
	@RequiresPermissions("sys:role:batchRemove")
	@RequestMapping("/batchRemove")
	@ResponseBody
	Response<String> batchRemove(@Size(min = 1) @RequestParam("ids[]") Integer[] ids) {
		if (sysRoleService.batchRemove(ids)) {
			return Response.ok();
		}
		throw new BusinessException("删除失败");
	}
}
