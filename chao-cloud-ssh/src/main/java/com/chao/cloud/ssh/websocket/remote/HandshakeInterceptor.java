package com.chao.cloud.ssh.websocket.remote;

import java.util.Map;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

/**
 * 创建日期:2018年1月11日<br/>
 * 创建时间:下午10:15:48<br/>
 * 创建者    :yellowcong<br/>
 * 机能概要:创建握手，类似与http的连接 三次握手一次挥手
 */
@Component
public class HandshakeInterceptor extends HttpSessionHandshakeInterceptor {

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
            Exception ex) {
        super.afterHandshake(request, response, wsHandler, ex);
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest arg0, ServerHttpResponse arg1, WebSocketHandler arg2,
            Map<String, Object> arg3) throws Exception {
        return super.beforeHandshake(arg0, arg1, arg2, arg3);
    }

}
