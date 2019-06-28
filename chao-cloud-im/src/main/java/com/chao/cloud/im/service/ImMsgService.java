package com.chao.cloud.im.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chao.cloud.im.dal.entity.ImMsg;
import com.chao.cloud.im.websocket.model.WsMsgVO;

/**
 * @功能：
 * @author： 超君子
 * @时间：2019-06-26
 * @version 1.0.0
 */
public interface ImMsgService extends IService<ImMsg> {

	/**
	 * 添加离线消息 
	 * @param vo 消息
	 * @param receiveIds 接收者
	 */
	void saveOffLineMsg(WsMsgVO vo, List<Integer> receiveIds);

}
