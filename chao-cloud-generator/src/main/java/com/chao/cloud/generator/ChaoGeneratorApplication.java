package com.chao.cloud.generator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import com.chao.cloud.common.config.exception.EnableGlobalException;
import com.chao.cloud.common.config.web.EnableWeb;
import com.chao.cloud.common.extra.mybatis.annotation.EnableMybatisGenerator;
import com.chao.cloud.common.extra.mybatis.annotation.EnableMybatisPlus;
import com.chao.cloud.common.extra.token.annotation.EnableFormToken;

/**
 * 
 * @功能：代码生成插件
 * @author： 薛超
 * @时间： 2019年7月23日
 * @version 1.0.0
 */
@SpringBootApplication
@EnableCaching // 缓存
@EnableWeb // web
@EnableGlobalException // 全局异常处理
@EnableFormToken // token
@EnableMybatisPlus // mybatis
@EnableMybatisGenerator // 代码生成
@EnableScheduling // 定时器
public class ChaoGeneratorApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChaoGeneratorApplication.class, args);
	}

	/**
	* 开启WebSocket支持
	* @return
	*/
	@Bean
	@ConditionalOnMissingBean(ServerEndpointExporter.class)
	public ServerEndpointExporter serverEndpointExporter() {
		return new ServerEndpointExporter();
	}
}
