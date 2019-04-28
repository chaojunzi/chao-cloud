package com.chao.cloud.ssh.controller;

import javax.validation.constraints.NotBlank;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chao.cloud.common.entity.Response;
import com.chao.cloud.common.entity.ResponseResult;

import lombok.extern.slf4j.Slf4j;

/**
 * 表情判断
 * @功能：
 * @author： 薛超
 * @时间：2019年4月24日
 * @version 1.0.0
 */
@RequestMapping("emoji")
@RestController
@Validated
@Slf4j
public class EmojiController {

    @RequestMapping("/send")
    public Response<String> emoji(@NotBlank(message = "text 不能为空") String text) {
        log.info("[emoji={}]", text);
        return ResponseResult.getResponseResult(text);
    }
}
