package com.chao.cloud.im.websocket.model;

import java.util.List;

import cn.hutool.core.util.ReflectUtil;
import lombok.Builder;
import lombok.Data;

@Data
public class LayimModel {

	private User mine;// 个人信息
	private List<Friend> friend;// 好友列表
	private List<Group> group;// 群

	public static User userConvert(Object obj) {
		User user = new User();
		ReflectUtil.setFieldValue(user, "id", ReflectUtil.getFieldValue(obj, "id"));
		ReflectUtil.setFieldValue(user, "username", ReflectUtil.getFieldValue(obj, "nickName"));
		ReflectUtil.setFieldValue(user, "sign", ReflectUtil.getFieldValue(obj, "sign"));
		ReflectUtil.setFieldValue(user, "avatar", ReflectUtil.getFieldValue(obj, "headImg"));
		return user;
	}

	@Data
	public static class User {// 用户
		private Integer id;
		private String username;// 昵称
		private String status;// 状态 若值为offline代表离线，online或者不填为在线
		private String sign;// 签名
		private String avatar;// 头像
	}

	@Data
	public static class Friend {// 好友
		private Integer id;
		private String groupname;// 群组昵称
		private List<User> list;// 好友列表
	}

	@Data
	public static class Group {// 群组
		private Integer id;
		private String groupname;// 昵称
		private String avatar;// 头像
	}

	@Data
	@Builder
	public static class Member {// 群成员
		private List<User> list;// 列表
	}

}
