package com.chao.cloud.admin.sys.controller;

import org.springframework.stereotype.Controller;

import com.chao.cloud.admin.sys.domain.dto.UserDTO;
import com.chao.cloud.admin.sys.shiro.ShiroUtils;

@Controller
public class BaseController {

	public static final String RANDOMCODEKEY = "RANDOMVALIDATECODEKEY";// 放到session中的key

	public UserDTO getUser() {
		return ShiroUtils.getUser();  
	}

	public Long getUserId() {
		return getUser().getUserId();
	}

	public String getUsername() {
		return getUser().getUsername();
	}
}