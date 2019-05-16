package com.chao.cloud.admin.system.controller;

import org.springframework.stereotype.Controller;

import com.chao.cloud.admin.system.domain.dto.UserDTO;
import com.chao.cloud.admin.system.shiro.ShiroUtils;

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