package com.chao.cloud.admin.sys.controller;

import java.util.List;

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

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.api.R;
import com.chao.cloud.admin.sys.constant.AdminConstant;
import com.chao.cloud.admin.sys.dal.entity.SysDept;
import com.chao.cloud.admin.sys.dal.entity.SysUser;
import com.chao.cloud.admin.sys.log.AdminLog;
import com.chao.cloud.admin.sys.service.SysDeptService;
import com.chao.cloud.admin.sys.service.SysUserService;
import com.chao.cloud.common.entity.Response;
import com.chao.cloud.common.entity.ResponseResult;
import com.chao.cloud.common.exception.BusinessException;

import cn.hutool.core.collection.CollUtil;

/** 
 * 部门管理 
 * @功能：
 * @author： 薛超
 * @时间：2019年5月9日 
 * @version 2.0
 */
@Controller
@RequestMapping("/sys/dept")
@Validated
public class DeptController extends BaseController {
	private String prefix = "sys/dept";
	@Autowired
	private SysDeptService sysDeptService;
	@Autowired
	private SysUserService sysUserService;

	@GetMapping()
	@RequiresPermissions("system:sysDept:sysDept")
	String dept() {
		return prefix + "/dept";
	}

	@AdminLog(AdminLog.STAT_PREFIX + "部门列表")
	@ResponseBody
	@GetMapping("/list")
	@RequiresPermissions("system:sysDept:sysDept")
	public R<List<SysDept>> list() {
		return R.ok(sysDeptService.list());
	}

	@GetMapping("/choose")
	String deptChoose() {
		return prefix + "/choose";
	}

	@GetMapping("/add/{pId}")
	@RequiresPermissions("system:sysDept:add")
	String add(@PathVariable("pId") Long pId, Model model) {
		model.addAttribute("pId", pId);
		if (pId == 0) {
			model.addAttribute("pName", "总部门");
		} else {
			model.addAttribute("pName", sysDeptService.getById(pId).getName());
		}
		return prefix + "/add";
	}

	@GetMapping("/edit/{deptId}")
	@RequiresPermissions("system:sysDept:edit")
	String edit(@PathVariable("deptId") Long deptId, Model model) {
		SysDept sysDept = sysDeptService.getById(deptId);
		model.addAttribute("sysDept", sysDept);
		if (AdminConstant.DEPT_ROOT_ID.equals(sysDept.getParentId())) {
			model.addAttribute("parentDeptName", "无");
		} else {
			SysDept parDept = sysDeptService.getById(sysDept.getParentId());
			model.addAttribute("parentDeptName", parDept.getName());
		}
		return prefix + "/edit";
	}

	/**
	 * 保存
	 */
	@ResponseBody
	@PostMapping("/save")
	@RequiresPermissions("system:sysDept:add")
	public Response<String> save(SysDept sysDept) {
		if (sysDeptService.save(sysDept)) {
			return ResponseResult.ok();
		}
		return ResponseResult.error();
	}

	/**
	 * 修改
	 */
	@ResponseBody
	@RequestMapping("/update")
	@RequiresPermissions("system:sysDept:edit")
	public Response<String> update(SysDept sysDept) {
		if (sysDeptService.updateById(sysDept)) {
			return ResponseResult.ok();
		}
		return ResponseResult.error();
	}

	/**
	 * 删除
	 */
	@PostMapping("/remove")
	@ResponseBody
	@RequiresPermissions("system:sysDept:remove")
	public Response<String> remove(@NotNull Long deptId) {
		int deptCount = sysDeptService.count(Wrappers.<SysDept>lambdaQuery().eq(SysDept::getParentId, deptId));
		if (deptCount > 0) {
			throw new BusinessException("部门包含用户,不允许修改");
		}

		int userCount = sysUserService.count(Wrappers.<SysUser>lambdaQuery().eq(SysUser::getDeptId, deptId));
		if (userCount > 0) {
			throw new BusinessException("部门包含用户,不允许修改");
		}

		if (sysDeptService.removeById(deptId)) {
			return ResponseResult.ok();
		}
		return ResponseResult.error();
	}

	/**
	 * 删除
	 */
	@PostMapping("/batchRemove")
	@ResponseBody
	@RequiresPermissions("system:sysDept:batchRemove")
	public Response<String> remove(@Size(min = 1) @RequestParam("ids[]") Long[] deptIds) {
		sysDeptService.removeByIds(CollUtil.toList(deptIds));
		return ResponseResult.ok();
	}

}
