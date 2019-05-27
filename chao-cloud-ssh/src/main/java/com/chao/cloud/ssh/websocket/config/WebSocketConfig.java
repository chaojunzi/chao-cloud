package com.chao.cloud.ssh.websocket.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import com.chao.cloud.ssh.websocket.remote.SshShellHandler;

import lombok.Data;

/**
 * 开启WebSocket支持
 * @功能：
 * @author： 薛超
 * @时间： 2019年5月27日
 * @version 1.0.0
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

	@Bean
	@ConfigurationProperties(prefix = "chao.cloud.ssh")
	public SshConfig sshConfig() {
		return new SshConfig();
	}

	/**
	 * ----------------模式1-----------------------------------
	 */
	@Bean
	public ServerEndpointExporter serverEndpointExporter() {
		return new ServerEndpointExporter();
	}

	/**
	 * ----------------模式2-----------------------------------
	 */

	@Autowired
	private SshShellHandler sshShellHandler;

	/**
	 * 配置socket解析器
	 */
	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		// 注册
		registry.addHandler(sshShellHandler, "/webshh/remote").setAllowedOrigins("*");// 跨域

	}

	@Data
	public static class SshConfig {
		private String host;
		private String username;
		private String password;
		private int port = 22;

	}

}