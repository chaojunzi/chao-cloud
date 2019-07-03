package com.chao.cloud.im.controller;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.api.R;
import com.chao.cloud.im.ai.constant.AiConstant;
import com.chao.cloud.im.dal.entity.ImGroupUser;
import com.chao.cloud.im.dal.entity.ImMsgHis;
import com.chao.cloud.im.dal.entity.ImUser;
import com.chao.cloud.im.domain.dto.LoginDTO;
import com.chao.cloud.im.domain.vo.MsgHisVO;
import com.chao.cloud.im.service.ImGroupService;
import com.chao.cloud.im.service.ImGroupUserService;
import com.chao.cloud.im.service.ImMsgHisService;
import com.chao.cloud.im.service.ImUserService;
import com.chao.cloud.im.websocket.LayimChatWebSocket;
import com.chao.cloud.im.websocket.constant.ImConstant;
import com.chao.cloud.im.websocket.model.LayimModel;
import com.chao.cloud.im.websocket.model.LayimModel.Friend;
import com.chao.cloud.im.websocket.model.LayimModel.Group;
import com.chao.cloud.im.websocket.model.LayimModel.Member;
import com.chao.cloud.im.websocket.model.LayimModel.User;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;

@RequestMapping("layim")
@Controller
@Validated
public class LayimController extends BaseController {

	private final String friendKeyTemplate = "{}@{}";
	@Autowired
	private ImUserService imUserService;
	@Autowired
	private ImGroupService imGroupService;
	@Autowired
	private ImGroupUserService imGroupUserService;
	@Autowired
	private LayimChatWebSocket singleChatWebSocket;
	@Autowired
	private ImMsgHisService imMsgHisService;

	@RequestMapping("/page/{html}")
	public String html(@PathVariable("html") String html) {
		return "layim/" + html;
	}

	@RequestMapping("/chatlog")
	public String chatlog(@Valid MsgHisVO vo, HttpSession session, Model m) {
		// 获取当前用户
		LoginDTO user = getUser(session);
		// 去重
		Set<String> fromTo = CollUtil.newHashSet(ImMsgHisService.fromTo(user.getId(), vo.getId()),
				ImMsgHisService.fromTo(vo.getId(), user.getId()));
		// 构造条件
		LambdaQueryWrapper<ImMsgHis> queryWrapper = Wrappers.lambdaQuery();
		queryWrapper.in(ImMsgHis::getFromTo, fromTo);
		queryWrapper.eq(ImMsgHis::getType, vo.getType());
		// 获取数据总数
		int count = imMsgHisService.count(queryWrapper);
		m.addAttribute("count", count);
		return "layim/chatlog";
	}

	/**
	 * 初始化参数
	 * @param html
	 * @return
	 */
	@RequestMapping("/initData")
	@ResponseBody
	public R<LayimModel> initData(HttpSession session) {
		LayimModel model = new LayimModel();
		// 获取自己-好友列表-群组列表
		User mine = LayimModel.userConvert(getUser(session));
		mine.setStatus(ImConstant.LineStatus.ON);// 在线
		// mine
		model.setMine(mine);
		// friend
		List<ImUser> list = imUserService.list();
		// 分组->归类
		if (CollUtil.isNotEmpty(list)) {
			Map<String, List<ImUser>> friendMap = list.stream().collect(
					Collectors.groupingBy(iu -> StrUtil.format(friendKeyTemplate, iu.getDeptId(), iu.getDeptName())));
			Set<Integer> onlineKey = singleChatWebSocket.onlineKey();
			// 设置在线状态
			List<LayimModel.Friend> friends = CollUtil.newArrayList();
			friendMap.forEach((k, v) -> {
				String[] dept = k.split("@");
				Friend friend = new LayimModel.Friend();
				friend.setId(Integer.valueOf(dept[0]));
				friend.setGroupname(dept[1]);
				List<User> users = v.stream().map(u -> {
					User user = LayimModel.userConvert(u);
					user.setStatus(AiConstant.REBOT_ID_LIST.contains(u.getId()) || onlineKey.contains(u.getId())
							? ImConstant.LineStatus.ON
							: ImConstant.LineStatus.OFF);
					return user;
				}).collect(Collectors.toList());
				friend.setList(users);
				friends.add(friend);
			});
			model.setFriend(friends);
		}
		// group
		List<Group> groups = imGroupService.list().stream().map(g -> {
			Group group = new LayimModel.Group();
			group.setId(g.getId());
			group.setGroupname(g.getNickName());
			group.setAvatar(g.getHeadImg());
			return group;
		}).collect(Collectors.toList());
		model.setGroup(groups);
		return R.ok(model);
	}

	/**
	 * 群成员
	 * @param id 群id
	 * @return
	 */
	@RequestMapping("/members")
	@ResponseBody
	public R<LayimModel.Member> memebers(@NotNull Integer id) {
		// 根据群id 查询群下的会员
		List<ImGroupUser> groupUsers = imGroupUserService
				.list(Wrappers.<ImGroupUser>lambdaQuery().eq(ImGroupUser::getGroupId, id));
		List<User> list = Collections.emptyList();
		if (CollUtil.isNotEmpty(groupUsers)) {
			List<Integer> userIds = groupUsers.stream().map(ImGroupUser::getUserId).collect(Collectors.toList());
			list = imUserService.list(Wrappers.<ImUser>lambdaQuery().in(ImUser::getId, userIds)).stream()
					.map(LayimModel::userConvert).collect(Collectors.toList());
		}
		return R.ok(Member.builder().list(list).build());
	}

}