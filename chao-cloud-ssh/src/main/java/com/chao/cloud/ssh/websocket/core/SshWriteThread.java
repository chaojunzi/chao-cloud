package com.chao.cloud.ssh.websocket.core;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.chao.cloud.ssh.websocket.remote.SshSocketServer;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @功能：用于读取ssh输出的
 * @author： 薛超
 * @时间：2019年5月6日
 * @version 2.0
 */
@Slf4j
public class SshWriteThread implements Runnable {

    // 定义一个flag,来停止线程用
    private boolean isStop = false;

    // 接入输入流数据
    private InputStream in;

    // 用于输出数据
    private String sid;
    private WebSocketSession session;

    // 停止线程
    public void stopThread() {
        this.isStop = true;
    }

    public SshWriteThread(InputStream in, WebSocketSession session) {
        super();
        this.in = in;
        this.session = session;
    }

    public SshWriteThread(InputStream in, String sid) {
        super();
        this.in = in;
        this.sid = sid;
    }

    public void run() {
        try {
            // 读取数据
            while (!isStop && // 线程是否停止
                    ((session != null && // session 不是空的
                            session.isOpen()) || StrUtil.isNotBlank(sid))

            ) { // session是打开的状态
                // 获取到我们的session
                // session.sendMessage(new TextMessage(new
                // String(result.toString().getBytes("ISO-8859-1"),"UTF-8")));
                // 写数据到服务器端
                // session.sendMessage(new TextMessage(result));

                // 写数据到客户端
                writeToWeb(in);
            }
        } catch (Exception e) {
            log.error("[error--->{}]", e);
        }

    }

    /**
     * 写数据到web控制界面
     * @param in
     */
    private void writeToWeb(InputStream in) {

        try {
            // 定义一个缓存
            // 一个UDP 的用户数据报的数据字段长度为8192字节
            byte[] buff = new byte[8192];

            int len = 0;
            StringBuffer sb = new StringBuffer();
            while ((len = in.read(buff)) > 0) {
                // 设定从0 开始
                sb.setLength(0);

                // 读取数组里面的数据，进行补码
                for (int i = 0; i < len; i++) {
                    // 进行补码操作
                    char c = (char) (buff[i] & 0xff);
                    sb.append(c);
                }
                String line = new String(sb.toString().getBytes("ISO-8859-1"), "UTF-8");
                // 写数据到服务器端
                if (session == null) {
                    SshSocketServer.sendInfo(line, sid);
                } else {
                    session.sendMessage(new TextMessage(line));
                }
            }
        } catch (IOException e) {
            log.error("[error--->{}]", e);
        }
    }

}
