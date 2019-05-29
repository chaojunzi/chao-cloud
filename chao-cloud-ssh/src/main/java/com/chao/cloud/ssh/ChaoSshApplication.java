package com.chao.cloud.ssh;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.chao.cloud.common.config.exception.EnableGlobalException;
import com.chao.cloud.common.config.web.EnableWeb;
import com.chao.cloud.common.extra.access.annotation.EnableAccessLimit;
import com.chao.cloud.common.extra.emoji.annotation.EnableEmojiFilter;

/**
 * ssh-web
 * @功能：
 * @author： 薛超
 * @时间：2019年3月6日
 * @version 1.0.0
 */
@SpringBootApplication
@EnableGlobalException // 全局异常处理
@EnableWeb
@EnableAccessLimit // 接口访问限制
@EnableEmojiFilter // 过滤非法emoji
public class ChaoSshApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChaoSshApplication.class, args);
	}
}
