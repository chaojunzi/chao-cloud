package com.chao.cloud.im.controller;

import javax.validation.constraints.NotBlank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chao.cloud.common.entity.Response;
import com.chao.cloud.im.ai.service.TAiRobotService;

/**
 * @功能：机器人聊天
 * @author： 超君子
 * @时间：2019-07-03
 * @version 1.0.0
 */
@RequestMapping("/robot")
@RestController
@Validated
public class RobotController {

	@Autowired
	private TAiRobotService robotService;

	@RequestMapping("/text")
	public Response<String> text(@NotBlank(message = "请输入 text") String text) {
		String result = robotService.text(text);
		return Response.ok(result);
	}

}