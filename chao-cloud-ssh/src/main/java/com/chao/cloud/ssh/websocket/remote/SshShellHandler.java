package com.chao.cloud.ssh.websocket.remote;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.chao.cloud.ssh.websocket.config.WebSocketConfig.SshConfig;
import com.chao.cloud.ssh.websocket.core.Server;
import com.chao.cloud.ssh.websocket.core.SshClient;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @功能：
 * @author： 薛超
 * @时间：2019年5月6日
 * @version 2.0
 */
@Component
@Slf4j
public class SshShellHandler extends TextWebSocketHandler {

    @Autowired
    private SshConfig sshConfig;

    // 客户端
    SshClient client = null;

    // 关闭连接后的处理
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        // 关闭连接
        if (client != null) {
            client.disconnect();
        }
    }

    // 建立socket连接
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        // 做多个用户处理的时候，可以在这个地方来 ,判断用户id和
        // 配置服务器信息
        Server server = new Server(sshConfig.getHost(), sshConfig.getUsername(), sshConfig.getPassword());
        // 初始化客户端
        client = new SshClient(server, session);
        // 连接服务器
        client.connect();
    }

    // 处理socker发送过来的消息
    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        super.handleMessage(session, message);

        // 处理连接
        try {
            TextMessage msg = (TextMessage) message;
            // 当客户端不为空的情况
            if (client != null) {
                // receive a close cmd ?
                if (Arrays.equals("exit".getBytes(), msg.asBytes())) {

                    if (client != null) {
                        client.disconnect();
                    }

                    session.close();
                    return;
                }
                // 写入前台传递过来的命令，发送到目标服务器上
                client.write(new String(msg.asBytes(), "UTF-8"));
            }
        } catch (Exception e) {
            log.error("[error--->{}]", e);
            session.sendMessage(new TextMessage("An error occured, websocket is closed."));
            session.close();
        }
    }
}
