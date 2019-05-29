package com.chao.cloud.admin.sys.controller;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
import com.chao.cloud.common.entity.ResponseResult;
import com.chao.cloud.common.exception.BusinessException;

@RequestMapping("/sys/role")
@Controller
@Validated
public class RoleController extends BaseController {
	String prefix = "sys/role";
	@Autowired
	private SysRoleService sysRoleService;

	@RequiresPermissions("sys:role:role")
	@GetMapping
	String role() {
		return prefix + "/role";
	}

	@AdminLog(AdminLog.STAT_PREFIX + "权限列表")
	@RequiresPermissions("sys:role:role")
	@GetMapping("/list")
	@ResponseBody
	Response<IPage<SysRole>> list(Page<SysRole> page) {
		return ResponseResult.getResponseResult(sysRoleService.page(page));
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
		RoleDTO roleDO = sysRoleService.get(id);
		model.addAttribute("role", roleDO);
		return prefix + "/edit";
	}

	@AdminLog("保存角色")
	@RequiresPermissions("sys:role:add")
	@PostMapping("/save")
	@ResponseBody
	Response<String> save(RoleDTO role) {
		if (sysRoleService.save(role)) {
			return ResponseResult.ok();
		}
		throw new BusinessException("保存失败 ");
	}

	@AdminLog("更新角色")
	@RequiresPermissions("sys:role:edit")
	@PostMapping("/update")
	@ResponseBody
	Response<String> update(RoleDTO role) {
		if (sysRoleService.update(role)) {
			return ResponseResult.ok();
		}
		throw new BusinessException("更新失败 ");
	}

	@AdminLog("删除角色")
	@RequiresPermissions("sys:role:remove")
	@PostMapping("/remove")
	@ResponseBody
	Response<String> save(@NotNull Long id) {
		if (sysRoleService.remove(id)) {
			return ResponseResult.ok();
		}
		throw new BusinessException("删除失败");

	}

	@RequiresPermissions("sys:role:batchRemove")
	@AdminLog("批量删除角色")
	@PostMapping("/batchRemove")
	@ResponseBody
	Response<String> batchRemove(@Size(min = 1) @RequestParam("ids[]") Long[] ids) {
		if (sysRoleService.batchRemove(ids)) {
			return ResponseResult.ok();
		}
		throw new BusinessException("删除失败");
	}
}
