package com.chao.cloud.ssh.websocket.remote;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.springframework.stereotype.Component;

import com.chao.cloud.common.core.SpringContextUtil;
import com.chao.cloud.ssh.websocket.config.WebSocketConfig.SshConfig;
import com.chao.cloud.ssh.websocket.core.Server;
import com.chao.cloud.ssh.websocket.core.SshClient;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;

@Component
@ServerEndpoint("/ssh/{sid}")
public class SshSocketServer {

    static Log log = LogFactory.get(SshSocketServer.class);
    // 静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    private static int onlineCount = 0;
    // concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
    private static CopyOnWriteArraySet<SshSocketServer> webSocketSet = new CopyOnWriteArraySet<SshSocketServer>();

    // 与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;

    // 接收sid
    private String sid = "";

    // 客户端
    SshClient client = null;

    /**
     * 连接建立成功调用的方法*/
    @OnOpen
    public void onOpen(Session session, @PathParam("sid") String sid) {
        this.session = session;
        webSocketSet.add(this); // 加入set中
        addOnlineCount(); // 在线数加1
        log.info("有新窗口开始监听:" + sid + ",当前在线人数为" + getOnlineCount());
        this.sid = sid;
        try {
            sendMessage("连接成功--->");
            // 做多个用户处理的时候，可以在这个地方来 ,判断用户id和
            SshConfig sshConfig = SpringContextUtil.getBean(SshConfig.class);
            // 配置服务器信息
            Server server = new Server(sshConfig.getHost(), sshConfig.getUsername(), sshConfig.getPassword());
            // 初始化客户端
            client = new SshClient(server, sid);
            // 连接服务器
            client.connect();
        } catch (IOException e) {
            log.error("websocket IO异常");
        }
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        webSocketSet.remove(this); // 从set中删除
        subOnlineCount(); // 在线数减1
        // 关闭连接
        if (client != null) {
            client.disconnect();
        }
        log.info("有一连接关闭！当前在线人数为" + getOnlineCount());
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     * @throws IOException */
    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        log.info("收到来自窗口" + sid + "的信息:" + message);
        // 处理连接
        try {
            // 当客户端不为空的情况
            if (client != null) {
                if ("exit".equals(message)) {
                    if (client != null) {
                        client.disconnect();
                    }
                    session.close();
                    return;
                }
                // 写入前台传递过来的命令，发送到目标服务器上
                client.write(message);
            }
        } catch (Exception e) {
            log.error("[error--->{}]", e);
            this.sendMessage("An error occured, websocket is closed.");
            session.close();
        }
        // 群发消息
        /*
         * for (WebSocketServer item : webSocketSet) { try {
         * item.sendMessage(message); } catch (IOException e) {
         * log.error("发生错误--->{}", e); } }
         */
    }

    /**
     * 
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("发生错误--->{}", error);
    }

    /**
     * 实现服务器主动推送
     */
    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

    /**
     * 群发自定义消息
     * */
    public static void sendInfo(String message, @PathParam("sid") String sid) throws IOException {
        log.info("推送消息到窗口" + sid + "，推送内容:" + message);
        for (SshSocketServer item : webSocketSet) {
            try {
                // 这里可以设定只推送给这个sid的，为null则全部推送
                if (sid == null) {
                    item.sendMessage(message);
                } else if (item.sid.equals(sid)) {
                    item.sendMessage(message);
                }
            } catch (IOException e) {
                continue;
            }
        }
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        SshSocketServer.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        SshSocketServer.onlineCount--;
    }

}
