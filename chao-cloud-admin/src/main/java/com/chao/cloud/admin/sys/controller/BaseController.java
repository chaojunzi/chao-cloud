package com.chao.cloud.admin.sys.controller;

import java.util.List;
import java.util.stream.Collectors;

import com.chao.cloud.admin.sys.domain.dto.UserDTO;
import com.chao.cloud.admin.sys.shiro.ShiroUtils;

import cn.hutool.core.collection.CollUtil;

public class BaseController {

	public static final String RANDOMCODEKEY = "RANDOMVALIDATECODEKEY";// 放到session中的key

	public UserDTO getUser() {
		return ShiroUtils.getUser();
	}

	public Integer getUserId() {
		return getUser().getUserId();
	}

	public String getRoles() {
		return getUser().getRoles();
	}

	public String getUsername() {
		return getUser().getUsername();
	}

	public List<Integer> removeEmpty(List<Integer> list) {
		if (CollUtil.isNotEmpty(list)) {
			return list.stream().distinct().filter(l -> l != null).collect(Collectors.toList());
		}
		return null;
	}
}