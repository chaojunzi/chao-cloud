package com.chao.cloud.common.websocket;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.Session;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SuppressWarnings("unchecked")
public abstract class BaseWebSocket<T> {

	private static final Map<Class<? extends BaseWebSocket<?>>, Map<?, BaseWebSocket<?>>> pool = new ConcurrentHashMap<>();
	// concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
	protected Map<T, BaseWebSocket<T>> webSocketMap = getWebSocketMap(this.getClass());
	// 与某个客户端的连接会话，需要通过它来给客户端发送数据
	@Getter
	protected Session session;
	// 接收sid
	protected T sid;

	public abstract boolean isExit();// 系统是否退出

	/**
	 * 连接建立成功调用的方法
	 */
	public final void open(Session session, T sid) {
		// 系统已经退出
		this.session = session;
		if (this.isExit()) {
			this.exit();
		}
		webSocketMap.put(sid, this); // 加入set中
		this.sid = sid;
		try {
			log.info("[{}:有新用户开始连接:user={},当前在线人数为:{}]", serverName(), sid, webSocketMap.size());
			sendMessage(WsMsgDTO.buildMsg(MsgEnum.START, "连接成功!"));
		} catch (Exception e) {
			log.error("[{}:user={},websocket IO异常:{}]", serverName(), sid, e.getMessage());
		}
	}

	/**
	 * 连接关闭调用的方法
	 */
	protected void onClose() {
		webSocketMap.remove(this.sid); // 从set中删除
		log.info("{}:有一连接关闭！当前在线人数为:{}", serverName(), webSocketMap.size());
		IoUtil.close(session);
	}

	/**
	 * 收到客户端消息后调用的方法
	 *
	 * @param msg 客户端发送过来的消息
	 * @throws IOException
	 */
	protected void onMessage(String msg, Session session) {
		log.info("{}:收到来自用户[{}]的信息:{}", serverName(), sid, msg);
	}

	protected void onError(Session session, Throwable error) {
		IoUtil.close(session);
		IoUtil.close(this.session);
		log.error("【{}】: 发生错误", serverName(), error);
	}

	/**
	 * 实现服务器主动推送(同步发送)
	 */
	public void sendMessage(Object msg) {
		try {
			this.session.getBasicRemote().sendText(JSONUtil.toJsonStr(msg));
		} catch (Exception e) {
			throw ExceptionUtil.wrapRuntime(e);
		}
	}

	public void sendAsyncMessage(Object msg) {
		try {
			// 异步发送
			this.session.getAsyncRemote().sendText(JSONUtil.toJsonStr(msg));
		} catch (Exception e) {
			throw ExceptionUtil.wrapRuntime(e);
		}
	}

	protected String serverName() {
		return StrUtil.lowerFirst(this.getClass().getSimpleName());
	}

	/**
	 * 是否存在
	 *
	 * @param sid
	 * @return
	 */
	protected boolean exist(T sid) {
		return webSocketMap.containsKey(sid);
	}

	protected void exit() {
		try {
			session.getAsyncRemote().sendText(JSONUtil.toJsonStr(WsMsgDTO.buildMsg(MsgEnum.CLOSE, "系统已经退出！")));
			if (session.isOpen()) {
				session.close();
			}
		} catch (Exception e) {
			throw ExceptionUtil.wrapRuntime(e);
		}
	}

	public T getSid() {
		return sid;
	}

	@SuppressWarnings("rawtypes")
	protected static Map getWebSocketMap(Class clazz) {
		Map map = pool.get(clazz);
		if (map == null) {
			synchronized (BaseWebSocket.class) {
				map = pool.get(clazz);
				if (null == map) {
					map = new ConcurrentHashMap<>();
					pool.put(clazz, map);
				}
			}
		}
		return map;
	}

	public static void closeAll() {
		Collection<Map<?, BaseWebSocket<?>>> poolMap = pool.values();
		if (CollUtil.isEmpty(poolMap)) {
			return;
		}
		poolMap.forEach(p -> {
			p.forEach((k, socket) -> {
				// 开始退出
				log.info("【websocket 关闭】: {}", k);
				try {
					socket.exit();
				} catch (Exception e) {
					log.error("【websocket网络连接关闭异常】", e);
				}
			});
		});

	}

}