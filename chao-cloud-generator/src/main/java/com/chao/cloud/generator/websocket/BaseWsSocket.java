package com.chao.cloud.generator.websocket;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.Session;
import javax.websocket.server.PathParam;

import com.chao.cloud.common.exception.BusinessException;
import com.chao.cloud.generator.websocket.model.MsgEnum;
import com.chao.cloud.generator.websocket.model.WsMsgDTO;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.StaticLog;

public abstract class BaseWsSocket<T> {

	private static Map<Class<? extends BaseWsSocket>, Map> pool = new ConcurrentHashMap<>();
	// concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
	protected Map<T, BaseWsSocket<T>> webSocketSet = getWsSocketMap(this.getClass());
	// 与某个客户端的连接会话，需要通过它来给客户端发送数据
	protected Session session;
	// 接收sid
	protected T sid;

	/**
	 * 连接建立成功调用的方法*/
	protected void onOpen(Session session, @PathParam("sid") T sid) {
		this.session = session;
		webSocketSet.put(sid, this); // 加入set中
		this.sid = sid;
		try {
			StaticLog.info("[{}:有新用户开始连接:user={},当前在线人数为:{}]", serverName(), sid, webSocketSet.size());
			sendMessage(WsMsgDTO.buildMsg(MsgEnum.START, "连接成功!"));
		} catch (Exception e) {
			StaticLog.error("[{}:user={},websocket IO异常:{}]", serverName(), sid, e.getMessage());
		}
	}

	/**
	 * 连接关闭调用的方法
	 */
	protected void onClose() {
		webSocketSet.remove(this.sid); // 从set中删除
		StaticLog.info("{}:有一连接关闭！当前在线人数为:{}", serverName(), webSocketSet.size());
	}

	/**
	 * 收到客户端消息后调用的方法
	 *
	 * @param message 客户端发送过来的消息
	 * @throws IOException */
	protected void onMessage(String msg, Session session) {
		StaticLog.info("{}:收到来自用户[{}]的信息:{}", serverName(), sid, msg);
	}

	protected void onError(Session session, Throwable error) {
		StaticLog.error("{}:发生错误--->{}", serverName(), error);
	}

	/**
	 * 实现服务器主动推送
	 */
	public void sendMessage(Object msg) {
		try {
			this.session.getBasicRemote().sendText(JSONUtil.toJsonStr(msg));
		} catch (Exception e) {
			throw new BusinessException(e.getMessage());
		}
	}

	protected String serverName() {
		return StrUtil.lowerFirst(this.getClass().getSimpleName());
	};

	/**
	 * 是否存在
	 * @param sid
	 * @return
	 */
	protected boolean exist(T sid) {
		return webSocketSet.containsKey(sid);
	}

	public T getSid() {
		return sid;
	}

	private static Map getWsSocketMap(Class<? extends BaseWsSocket> clazz) {
		Map map = pool.get(clazz);
		if (map == null) {
			synchronized (BaseWsSocket.class) {
				map = pool.get(clazz);
				if (null == map) {
					map = new ConcurrentHashMap<>();
					pool.put(clazz, map);
				}
			}
		}
		return map;
	}

}