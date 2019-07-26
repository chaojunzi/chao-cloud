package com.chao.cloud.im.controller;

import java.util.Set;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chao.cloud.common.entity.Response;
import com.chao.cloud.im.dal.entity.ImMsgHis;
import com.chao.cloud.im.domain.dto.LoginDTO;
import com.chao.cloud.im.domain.vo.MsgHisVO;
import com.chao.cloud.im.service.ImMsgHisService;

import cn.hutool.core.collection.CollUtil;

/**
 * @功能：历史记录
 * @author： 超君子
 * @时间：2019-06-28
 * @version 1.0.0
 */
@RequestMapping("/im/msg_his")
@RestController
@Validated
public class MsgHisController extends BaseController {

	@Autowired
	private ImMsgHisService imMsgHisService;

	@RequestMapping("/list")
	public Response<IPage<ImMsgHis>> list(Page<ImMsgHis> page, // 分页
			HttpSession session, @Valid MsgHisVO vo) {
		// 获取当前用户
		LoginDTO user = getUser(session);
		// 去重
		Set<String> fromTo = CollUtil.newHashSet(ImMsgHisService.fromTo(user.getId(), vo.getId()),
				ImMsgHisService.fromTo(vo.getId(), user.getId()));
		// 构造条件
		LambdaQueryWrapper<ImMsgHis> queryWrapper = Wrappers.lambdaQuery();
		queryWrapper.in(ImMsgHis::getFromTo, fromTo);
		queryWrapper.eq(ImMsgHis::getType, vo.getType());
		return Response.ok(imMsgHisService.page(page, queryWrapper));
	}

}