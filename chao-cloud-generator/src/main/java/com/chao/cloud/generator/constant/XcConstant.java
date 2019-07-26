package com.chao.cloud.generator.constant;

import java.util.List;

import cn.hutool.core.collection.CollUtil;

/**
 * 
 * @功能：常量
 * @author： 薛超
 * @时间： 2019年7月25日
 * @version 1.0.2
 */
public interface XcConstant {

	List<String> ADMIN_USER_NAME = CollUtil.toList("xuechao");

	String ADMIN_MENU = "<a link=\"/xc/user\" href=\"javascript:;\">用户管理</a>";

	/**
	 * 状态
	 */
	interface Status {
		/**
		 * 冻结
		 */
		Integer FREEZE = 0;
		/**
		 * 正常
		 */
		Integer OK = 1;
	}

}
