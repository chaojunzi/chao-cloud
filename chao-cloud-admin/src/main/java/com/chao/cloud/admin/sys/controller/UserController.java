package com.chao.cloud.admin.sys.controller;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chao.cloud.admin.sys.constant.AdminConstant;
import com.chao.cloud.admin.sys.dal.entity.SysUser;
import com.chao.cloud.admin.sys.domain.dto.RoleDTO;
import com.chao.cloud.admin.sys.domain.dto.UserDTO;
import com.chao.cloud.admin.sys.domain.vo.UserVO;
import com.chao.cloud.admin.sys.log.AdminLog;
import com.chao.cloud.admin.sys.service.SysRoleService;
import com.chao.cloud.admin.sys.service.SysUserService;
import com.chao.cloud.common.entity.Response;
import com.chao.cloud.common.entity.ResponseResult;
import com.chao.cloud.common.exception.BusinessException;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;

@RequestMapping("/sys/user")
@Controller
@Validated
public class UserController extends BaseController {
	private String prefix = "sys/user";
	@Autowired
	SysUserService sysUserService;
	@Autowired
	SysRoleService sysRoleService;

	@RequiresPermissions("sys:user:user")
	@GetMapping("")
	String user(Model model) {
		return prefix + "/user";
	}

	@GetMapping("/list")
	@AdminLog(AdminLog.STAT_PREFIX + "用户列表")
	@ResponseBody
	Response<IPage<SysUser>> list(Page<SysUser> page, String username) {
		// 查询列表数据
		LambdaQueryWrapper<SysUser> queryWrapper = Wrappers.lambdaQuery();
		if (StrUtil.isNotBlank(username)) {
			queryWrapper.like(SysUser::getUsername, username);
		}
		IPage<SysUser> result = sysUserService.page(page, queryWrapper);
		if (CollUtil.isNotEmpty(result.getRecords())) {
			List<SysUser> records = result.getRecords().stream()
					.filter(u -> !AdminConstant.ADMIN_ID.equals(u.getUserId())).collect(Collectors.toList());
			result.setRecords(records);
		}
		return ResponseResult.getResponseResult(result);
	}

	@RequiresPermissions("sys:user:add")
	@AdminLog("添加用户")
	@GetMapping("/add")
	String add(Model model) {
		List<RoleDTO> roles = sysRoleService.list(Collections.emptyList());
		model.addAttribute("roles", roles);
		return prefix + "/add";
	}

	@RequiresPermissions("sys:user:edit")
	@AdminLog("编辑用户")
	@GetMapping("/edit/{id}")
	String edit(Model model, @PathVariable("id") Long id) {
		UserDTO user = sysUserService.get(id);
		model.addAttribute("user", user);
		List<RoleDTO> roles = sysRoleService.list(user.getRoleIds());
		model.addAttribute("roles", roles);
		return prefix + "/edit";
	}

	@RequiresPermissions("sys:user:resetPwd")
	@AdminLog("请求更改用户密码")
	@GetMapping("/resetPwd/page")
	String resetPwd() {
		return prefix + "/reset_pwd";
	}

	@RequiresPermissions("sys:user:add")
	@AdminLog("保存用户")
	@PostMapping("/save")
	@ResponseBody
	Response<String> save(UserDTO user) {
		String password = DigestUtil.md5Hex(user.getUsername() + user.getPassword());
		user.setPassword(password);
		if (sysUserService.save(user) > 0) {
			return ResponseResult.ok();
		}
		throw new BusinessException("保存失败");
	}

	@RequiresPermissions("sys:user:edit")
	@AdminLog("更新用户")
	@PostMapping("/update")
	@ResponseBody
	Response<String> update(UserDTO user) {
		if (sysUserService.update(user) > 0) {
			return ResponseResult.ok();
		}
		throw new BusinessException("更新失败");
	}

	@RequiresPermissions("sys:user:remove")
	@AdminLog("删除用户")
	@PostMapping("/remove")
	@ResponseBody
	Response<String> remove(@NotNull Long id) {
		boolean isAdmin = id.equals(AdminConstant.ADMIN_ID);
		if (isAdmin) {
			throw new BusinessException("admin 不可删除");
		}
		if (sysUserService.remove(id) > 0) {
			return ResponseResult.ok();
		}
		throw new BusinessException("删除失败");
	}

	@RequiresPermissions("sys:user:batchRemove")
	@AdminLog("批量删除用户")
	@PostMapping("/batchRemove")
	@ResponseBody
	Response<String> batchRemove(@RequestParam("ids[]") @Size(min = 1) Long[] userIds) {
		boolean hasAdmin = ArrayUtil.contains(userIds, AdminConstant.ADMIN_ID);
		if (hasAdmin) {
			throw new BusinessException("admin 不可删除");
		}
		int r = sysUserService.removeBatch(userIds);
		if (r > 0) {
			return ResponseResult.ok();
		}
		throw new BusinessException("删除失败");
	}

	@AdminLog("提交更改用户密码")
	@PostMapping("/resetPwd")
	@ResponseBody
	Response<String> resetPwd(UserVO userVO) {
		sysUserService.resetPwd(userVO, getUser());
		return ResponseResult.ok();

	}

}
