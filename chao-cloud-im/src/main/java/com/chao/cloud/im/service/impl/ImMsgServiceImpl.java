package com.chao.cloud.im.service.impl;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chao.cloud.im.dal.entity.ImMsg;
import com.chao.cloud.im.dal.entity.ImMsgHis;
import com.chao.cloud.im.dal.mapper.ImMsgMapper;
import com.chao.cloud.im.service.ImMsgHisService;
import com.chao.cloud.im.service.ImMsgService;
import com.chao.cloud.im.websocket.LayimChatWebSocket;
import com.chao.cloud.im.websocket.model.WsMsgVO;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.BooleanUtil;

/**
 * @功能：
 * @author： 超君子
 * @时间：2019-06-26
 * @version 1.0.0
 */
@Service
public class ImMsgServiceImpl extends ServiceImpl<ImMsgMapper, ImMsg> implements ImMsgService {

	@Autowired
	private LayimChatWebSocket layimChatWebSocket;
	@Autowired
	private ImMsgHisService imMsgHisService;

	/**
	 * 异步消息推送
	 */
	@Async
	@Override
	@Transactional
	public void saveOffLineMsg(WsMsgVO vo, List<Integer> receiveIds) {
		boolean isSystem = BooleanUtil.isTrue(vo.getSystem());
		if (!isSystem) {// 非系统消息
			Set<Integer> key = layimChatWebSocket.onlineKey();
			// 遍历接收者->改变to的值
			List<ImMsg> list = receiveIds.stream().filter(k -> !key.contains(k))
					.map(BeanUtil.toBean(vo, ImMsg.class)::setToid).collect(Collectors.toList());
			// 批量添加
			if (CollUtil.isNotEmpty(list)) {
				this.saveBatch(list, list.size());
			}
			// 增加历史记录
			ImMsgHis msgHis = BeanUtil.toBean(vo, ImMsgHis.class)
					.setFromTo(ImMsgHisService.fromTo(vo.getFromid(), vo.getToid()));
			imMsgHisService.save(msgHis);
		}

	}

}
