package com.chao.cloud.ssh.websocket.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.chao.cloud.ssh.websocket.remote.HandshakeInterceptor;
import com.chao.cloud.ssh.websocket.remote.SshShellHandler;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Autowired
    private HandshakeInterceptor handshakeInterceptor;
    @Autowired
    private SshShellHandler sshShellHandler;

    /**
     * 配置socket解析器
     */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // 注册
        registry.addHandler(sshShellHandler, "/webshh/remote").addInterceptors(handshakeInterceptor)// 拦截器
                .setAllowedOrigins("*");// 跨域

    }
}