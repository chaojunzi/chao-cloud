package com.chao.cloud.common.websocket;

import java.io.File;
import java.util.Map;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.springframework.stereotype.Component;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.exceptions.ValidateException;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.Tailer;
import cn.hutool.core.util.URLUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 日志文件跟踪
 * 
 * @author 薛超
 * @since 2022年1月15日
 * @version 1.0.0
 */
@Slf4j
@Component
@ServerEndpoint("/ws/logTail/{path}")
public class LogTailWebSocket extends BaseWebSocket<String> {

	@SuppressWarnings("unchecked")
	private static final Map<String, LogTailWebSocket> webSocketMap = getWebSocketMap(LogTailWebSocket.class);

	private Tailer tailer;

	@OnOpen
	public void onOpen(Session session, @PathParam("path") String path) {
		sid = session.getId();
		//
		boolean exist = super.exist(sid);
		if (exist) {// 关闭连接
			this.alreadyConn(session);
			return;
		}
		// 连接websocket
		super.open(session, sid);
		//
		path = URLUtil.decode(path);
		log.info("【文件跟踪】: {}", path);
		File file = FileUtil.file(path);
		if (!file.exists()) {
			sendAsyncMessage(file);
			return;
			//
		}
		// 构造一个文件跟踪器
		tailer = new Tailer(file, line -> {
			sendAsyncMessage(WsMsgDTO.buildMsg(MsgEnum.RECEIVE, line));
		});
		// 异步处理
		tailer.start(true);
	}

	/**
	 * 发送消息
	 */
	public void sendMsg(String sid, WsMsgDTO msg) {
		BaseWebSocket<String> socket = webSocketMap.get(sid);
		if (socket != null) {
			socket.sendMessage(msg);
		}
	}

	/**
	 * 是否已经连接
	 */
	public boolean contains(String sid) {
		return webSocketMap.containsKey(sid);
	}

	/**
	 * 收到客户端消息后调用的方法
	 *
	 * @param message 客户端发送过来的消息
	 */
	@Override
	@OnMessage
	public void onMessage(String message, Session session) {
		log.info("收到来自窗口" + sid + "的信息:" + message);
		// 处理连接

	}

	// 已经连接
	private void alreadyConn(Session session) {
		try {
			session.getBasicRemote().sendText(JSONUtil.toJsonStr(WsMsgDTO.buildMsg(MsgEnum.CLOSE, "您已经在另一地方连接！")));
			if (session.isOpen()) {
				session.close();
			}
		} catch (Exception e) {
			throw ExceptionUtil.wrapRuntime(e);
		}
	}

	@Override
	@OnClose
	public void onClose() {
		try {
			super.onClose();
		} catch (Exception e) {
			log.error("【{}】: websocket 关闭失败！", this.sid, e);
		}
		closeTail();

	}

	@Override
	@OnError
	public void onError(Session session, Throwable error) {
		try {
			super.onError(session, error);
		} catch (Exception e) {
			log.error("【{}】: websocket 处理错误失败！", this.sid, e);
		}
		closeTail();
	}

	private void closeTail() {
		if (tailer != null) {
			tailer.stop();
		}
	}

	/**
	 * 下线
	 * 
	 */
	public void offline(String sid) {
		try {
			BaseWebSocket<String> socket = webSocketMap.get(sid);
			if (socket == null) {
				throw new ValidateException("您已经下线");
			}
			this.alreadyConn(socket.getSession());
		} catch (Exception e) {
			throw ExceptionUtil.wrapRuntime(e);
		}
	}

	/**
	 * 是否存在连接
	 */
	public static boolean hasConn() {
		return CollUtil.isNotEmpty(webSocketMap);
	}

	/**
	 * 给全体发送消息
	 * 
	 */
	public static void sendLogToAll(String msg) {
		if (hasConn()) {
			// 发送消息
			CollUtil.forEach(webSocketMap.values(), (ws, i) -> {
				try {
					ws.sendAsyncMessage(WsMsgDTO.buildMsg(MsgEnum.RECEIVE, msg));
				} catch (Exception e) {
					throw ExceptionUtil.wrapRuntime(e);
				}
			});
		}
	}

	@Override
	public boolean isExit() {
		return false;
	}

}
