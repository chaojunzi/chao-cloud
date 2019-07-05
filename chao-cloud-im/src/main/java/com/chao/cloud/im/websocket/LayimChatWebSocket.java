package com.chao.cloud.im.websocket;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.chao.cloud.common.core.SpringContextUtil;
import com.chao.cloud.common.exception.BusinessException;
import com.chao.cloud.common.util.EntityUtil;
import com.chao.cloud.im.ai.constant.AiConstant;
import com.chao.cloud.im.ai.service.AipUnitService;
import com.chao.cloud.im.ai.service.TAiRobotService;
import com.chao.cloud.im.ai.service.TAipImageClassifyService;
import com.chao.cloud.im.dal.entity.ImGroupUser;
import com.chao.cloud.im.dal.entity.ImMsg;
import com.chao.cloud.im.service.ImGroupUserService;
import com.chao.cloud.im.service.ImMsgService;
import com.chao.cloud.im.websocket.constant.ImConstant;
import com.chao.cloud.im.websocket.constant.MsgConstant;
import com.chao.cloud.im.websocket.constant.MsgEnum;
import com.chao.cloud.im.websocket.model.ImMsgDTO;
import com.chao.cloud.im.websocket.model.WsMsgDTO;
import com.chao.cloud.im.websocket.model.WsMsgVO;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.StaticLog;
import lombok.extern.slf4j.Slf4j;

/**
 * 单聊
 * @功能：
 * @author： 薛超
 * @时间： 2019年6月26日
 * @version 1.0.0
 */
@Slf4j
@Component
@ServerEndpoint("/im/chat/{sid}")
public class LayimChatWebSocket extends BaseWsSocket<Integer> {

	/**
	 * 连接建立成功调用的方法
	 *  
	 */
	@OnOpen
	public void onOpen(Session session, @PathParam("sid") Integer sid) {
		boolean exist = super.exist(sid);
		if (exist) {// 关闭连接
			this.alreadyLogin(session);
			return;
		}
		super.onOpen(session, sid);
		// 发送上线消息
		this.sendAll(WsMsgDTO.buildMsg(MsgEnum.ON_LINE, sid));
		// 推送离线消息
		this.pushOffMsg();
	}

	/**
	 * 收到客户端消息后调用的方法
	 *
	 * @param message 客户端发送过来的消息
	 * @throws IOException */
	@OnMessage
	public void onMessage(String msg, Session session) {
		super.onMessage(msg, session);
		WsMsgVO vo = JSONUtil.toBean(msg, WsMsgVO.class);
		// 分类发送消息
		if (!BeanUtil.isEmpty(vo)) {
			// 判断是否为机器人
			if (AiConstant.REBOT_ID_LIST.contains(vo.getToid()) && ImConstant.Type.FRIEND.equals(vo.getType())) {
				this.rebotReply(vo);
				return;
			}
			// 获取接收人
			List<Integer> receiveIds = CollUtil.toList(vo.getToid());
			if (ImConstant.Type.GROUP.equals(vo.getType())) {// 是否为group
				// 从容器中获取对象
				ImGroupUserService imGroupUserService = SpringContextUtil.getBean(ImGroupUserService.class);
				// 查询
				List<ImGroupUser> groupUsers = imGroupUserService
						.list(Wrappers.<ImGroupUser>lambdaQuery().eq(ImGroupUser::getGroupId, vo.getToid()));
				receiveIds = groupUsers.stream().map(ImGroupUser::getUserId)//
						.filter(uid -> !vo.getFromid().equals(uid))// 排除自己
						.collect(Collectors.toList());
			}
			// 添加数据库
			ImMsgService imMsgService = SpringContextUtil.getBean(ImMsgService.class);
			imMsgService.saveOffLineMsg(vo, receiveIds);
			// 发送
			sendMsg(WsMsgDTO.buildMsg(MsgEnum.CHAT, vo), receiveIds);
		}
	}

	@OnClose
	public void onClose() {
		super.onClose();
		// 发送下线消息
		this.sendAll(WsMsgDTO.buildMsg(MsgEnum.OFF_LINE, sid));
	}

	/**
	 * 给所有人发送消息
	 * @param msg
	 */
	public void sendAll(WsMsgDTO msg) {
		webSocketSet.forEach((sid, item) -> {
			try {
				item.sendMessage(msg);
			} catch (Exception e) {
				log.error("[消息发送失败: user={},msg={},error={}]", sid, msg, e.getMessage());
			}
		});

	}

	/**
	 * 群发自定义消息
	 * */
	public void sendMsg(WsMsgDTO msg, Collection<Integer> sids) {
		StaticLog.info("[push-msg-user={},msg={}.]", sids, msg);
		webSocketSet.forEach((sid, item) -> {
			try {
				boolean pass = CollUtil.isNotEmpty(sids) && sids.contains(sid);
				// 这里可以设定只推送给这个sid的，为null则全部推送
				if (pass) {
					item.sendMessage(msg);
				}
			} catch (Exception e) {
				log.error("[消息发送失败: user={},msg={},error={}]", sid, msg, e.getMessage());
			}
		});

	}

	@OnError
	public void onError(Session session, Throwable error) {
		super.onError(session, error);
	}

	public Map<Integer, Long> onlineList() {
		return webSocketSet.keySet().stream().collect(Collectors.groupingBy(k -> k, Collectors.counting()));

	}

	public Set<Integer> onlineKey() {
		return webSocketSet.keySet();
	}

	// 已经登录
	private void alreadyLogin(Session session) {
		try {
			session.getBasicRemote().sendText(JSONUtil.toJsonStr(WsMsgDTO.buildMsg(MsgEnum.CLOSE, "您已经登录")));
			if (session.isOpen()) {
				session.close();
			}
		} catch (Exception e) {
			throw new BusinessException(e.getMessage());
		}
	}

	/**
	 * 推送离线消息
	 * @param session
	 */
	private void pushOffMsg() {
		// 查询消息
		ImMsgService imMsgService = SpringContextUtil.getBean(ImMsgService.class);
		List<ImMsg> list = imMsgService.list(Wrappers.<ImMsg>lambdaQuery().eq(ImMsg::getToid, sid));
		if (CollUtil.isNotEmpty(list)) {
			super.sendMessage(WsMsgDTO.buildMsg(MsgEnum.OFF_MSG, EntityUtil.listConver(list, ImMsgDTO.class)));
			// 删除离线消息
			List<Integer> ids = list.stream().map(ImMsg::getMsgId).collect(Collectors.toList());
			imMsgService.removeByIds(ids);
		}
	}

	/**
	 * 机器人回复
	 * @param vo
	 */
	private void rebotReply(WsMsgVO vo) {
		// 设置对方正在输入
		sendMessage(WsMsgDTO.buildMsg(MsgEnum.OTHER_INPUT, ImConstant.OTHER_INPUT));
		// 判断是否为图片并进行解析 img[/img/2019/07/05/1147064232379027456.png]
		String content = vo.getContent();
		String imgName = null;

		if (ReUtil.isMatch(MsgConstant.LAYIM_FILE_REGEX, content)) {// 判断是否为文件
			// 判断是否为图片
			imgName = ReUtil.getGroup1(MsgConstant.LAYIM_FILE_REGEX, content);
			String mimeType = FileUtil.getMimeType(imgName);
			if (!mimeType.startsWith("image")) {
				reply(vo, StrUtil.format("{}也不知道这是个啥", vo.getToname()));
				return;
			}
		} else if (ReUtil.isMatch(MsgConstant.LAYIM_IMG_REGEX, content)) {
			imgName = ReUtil.getGroup1(MsgConstant.LAYIM_IMG_REGEX, content);
		}

		if (StrUtil.isNotBlank(imgName)) {
			// 解析图片
			TAipImageClassifyService imgClassifyService = SpringContextUtil.getBean(TAipImageClassifyService.class);
			try (InputStream in = URLUtil.getStream(URLUtil.toUrlForHttp(imgName))) {
				byte[] image = IoUtil.readBytes(in);
				String visionImg = imgClassifyService.visionImg(image);
				reply(vo, visionImg);
				log.info("[AI:图片解析结果={}]", visionImg);
			} catch (Exception e) {
				// 解析失败：返回
				log.error("{}", e);
				reply(vo, StrUtil.format("{}不知道这个图片是啥", vo.getToname()));
			}
			return;
		}

		// 获取解析结果
		String result = AiConstant.ERROR_RESULT;
		if (vo.getToid().equals(AiConstant.XUE)) {
			// 查询
			TAiRobotService robotService = SpringContextUtil.getBean(TAiRobotService.class);
			result = robotService.text(vo.getContent());
		} else if (vo.getToid().equals(AiConstant.CHAO)) {
			AipUnitService unitService = SpringContextUtil.getBean(AipUnitService.class);
			result = unitService.text(vo.getContent(), vo.getFromid());
		}
		reply(vo, result);

	}

	private void reply(WsMsgVO vo, String result) {
		// 回复
		vo.setId(vo.getToid());
		vo.setFromid(vo.getToid());
		vo.setUsername(vo.getToname());
		vo.setAvatar(vo.getToavatar());
		vo.setContent(result);
		sendMessage(WsMsgDTO.buildMsg(MsgEnum.CHAT, vo));

	}
}