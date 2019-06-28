package com.chao.cloud.im.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chao.cloud.im.dal.entity.ImMsgHis;

import cn.hutool.core.util.StrUtil;

/**
 * @功能：
 * @author： 超君子
 * @时间：2019-06-28
 * @version 1.0.0
 */
public interface ImMsgHisService extends IService<ImMsgHis> {
	/**
	 * 用户类型模板
	 */
	String USER_TEMPLATE = "{}-{}";

	static String fromTo(Integer from, Integer to) {
		return StrUtil.format(ImMsgHisService.USER_TEMPLATE, from, to);
	}

}
