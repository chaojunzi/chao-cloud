package com.chao.cloud.admin.system.controller;

import java.util.Collections;
import java.util.List;
import java.util.Map;
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

import com.chao.cloud.admin.system.annotation.AdminLog;
import com.chao.cloud.admin.system.constant.AdminConstant;
import com.chao.cloud.admin.system.domain.dto.RoleDTO;
import com.chao.cloud.admin.system.domain.dto.UserDTO;
import com.chao.cloud.admin.system.domain.vo.UserVO;
import com.chao.cloud.admin.system.service.RoleService;
import com.chao.cloud.admin.system.service.UserService;
import com.chao.cloud.admin.system.utils.Query;
import com.chao.cloud.admin.system.utils.R;
import com.chao.cloud.common.exception.BusinessException;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.crypto.digest.DigestUtil;

@RequestMapping("/sys/user")
@Controller
@Validated
public class UserController extends BaseController {
	private String prefix = "system/user";
	@Autowired
	UserService userService;
	@Autowired
	RoleService roleService;

	@RequiresPermissions("sys:user:user")
	@GetMapping("")
	String user(Model model) {
		return prefix + "/user";
	}

	@GetMapping("/list")
	@AdminLog(AdminLog.STAT_PREFIX + "用户列表")
	@ResponseBody
	R list(@RequestParam Map<String, Object> params) {
		// 查询列表数据
		Query query = new Query(params);
		int count = userService.count(query);
		if (count < 1) {
			return R.page(Collections.EMPTY_LIST, count);
		}
		List<UserDTO> sysUserList = userService.list(query);
		sysUserList = sysUserList.stream().filter(u -> u.getUserId() > 0).collect(Collectors.toList());
		return R.page(sysUserList, count);
	}

	@RequiresPermissions("sys:user:add")
	@AdminLog("添加用户")
	@GetMapping("/add")
	String add(Model model) {
		List<RoleDTO> roles = roleService.list(Collections.emptyMap());
		model.addAttribute("roles", roles);
		return prefix + "/add";
	}

	@RequiresPermissions("sys:user:edit")
	@AdminLog("编辑用户")
	@GetMapping("/edit/{id}")
	String edit(Model model, @PathVariable("id") Long id) {
		UserDTO userDO = userService.get(id);
		model.addAttribute("user", userDO);
		List<RoleDTO> roles = roleService.list(id, userDO.getRoleIds());
		model.addAttribute("roles", roles);
		return prefix + "/edit";
	}

	@RequiresPermissions("sys:user:add")
	@AdminLog("保存用户")
	@PostMapping("/save")
	@ResponseBody
	R save(UserDTO user) {
		String password = DigestUtil.md5Hex(user.getUsername() + user.getPassword());
		user.setPassword(password);
		if (userService.save(user) > 0) {
			return R.ok();
		}
		return R.error();
	}

	@RequiresPermissions("sys:user:edit")
	@AdminLog("更新用户")
	@PostMapping("/update")
	@ResponseBody
	R update(UserDTO user) {
		if (userService.update(user) > 0) {
			return R.ok();
		}
		return R.error();
	}

	@RequiresPermissions("sys:user:edit")
	@AdminLog("更新用户")
	@PostMapping("/updatePeronal")
	@ResponseBody
	R updatePeronal(UserDTO user) {
		if (userService.updatePersonal(user) > 0) {
			return R.ok();
		}
		return R.error();
	}

	@RequiresPermissions("sys:user:remove")
	@AdminLog("删除用户")
	@PostMapping("/remove")
	@ResponseBody
	R remove(@NotNull Long id) {
		boolean isAdmin = id.equals(AdminConstant.ADMIN_ID);
		if (isAdmin) {
			throw new BusinessException("admin 不可删除");
		}
		if (userService.remove(id) > 0) {
			return R.ok();
		}
		return R.error();
	}

	@RequiresPermissions("sys:user:batchRemove")
	@AdminLog("批量删除用户")
	@PostMapping("/batchRemove")
	@ResponseBody
	R batchRemove(@RequestParam("ids[]") @Size(min = 1) Long[] userIds) {
		boolean hasAdmin = ArrayUtil.contains(userIds, AdminConstant.ADMIN_ID);
		if (hasAdmin) {
			throw new BusinessException("admin 不可删除");
		}
		int r = userService.batchremove(userIds);
		if (r > 0) {
			return R.ok();
		}
		return R.error();
	}

	@PostMapping("/exit")
	@ResponseBody
	boolean exit(@RequestParam Map<String, Object> params) {
		// 存在，不通过，false
		return !userService.exit(params);
	}

	@RequiresPermissions("sys:user:resetPwd")
	@AdminLog("请求更改用户密码")
	@GetMapping("/resetPwd/page")
	String resetPwd() {
		return prefix + "/reset_pwd";
	}

	@AdminLog("提交更改用户密码")
	@PostMapping("/resetPwd")
	@ResponseBody
	R resetPwd(UserVO userVO) {
		try {
			userService.resetPwd(userVO, getUser());
			return R.ok();
		} catch (Exception e) {
			return R.error(1, e.getMessage());
		}

	}

	@GetMapping("/personal")
	String personal(Model model) {
		UserDTO userDO = userService.get(getUserId());
		model.addAttribute("user", userDO);
		model.addAttribute("hobbyList", "");
		model.addAttribute("sexList", "");
		return prefix + "/personal";
	}

}
