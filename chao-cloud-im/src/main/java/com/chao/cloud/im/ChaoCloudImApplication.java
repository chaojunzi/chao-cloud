package com.chao.cloud.im;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import com.chao.cloud.common.config.exception.EnableGlobalException;
import com.chao.cloud.common.config.web.EnableWeb;
import com.chao.cloud.common.extra.ftp.annotation.EnableFtp;
import com.chao.cloud.common.extra.mybatis.annotation.EnableMybatisGenerator;
import com.chao.cloud.common.extra.mybatis.annotation.EnableMybatisPlus;

/**
 * 
 * @功能：im -> web 聊天系统
 * @author： 薛超
 * @时间： 2019年6月24日
 * @version 1.0.0
 */
@SpringBootApplication
@EnableCaching // 缓存
@EnableWeb // web
@EnableGlobalException // 全局异常处理
@EnableMybatisPlus // 数据库插件-乐观锁
@EnableMybatisGenerator // 代码生成
@EnableAsync // 异步调用
@EnableFtp
public class ChaoCloudImApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChaoCloudImApplication.class, args);
	}

	/**
	* 开启WebSocket支持
	* @return
	*/
	@Bean
	public ServerEndpointExporter serverEndpointExporter() {
		return new ServerEndpointExporter();
	}

}
