package com.chao.cloud.common.base;

import cn.hutool.core.util.StrUtil;

public interface BaseStringOptional {
	/**
	 *  斜杠
	 */
	String VIRGULE = "/";

	/**
	 * 获取全路径
	 * @param imgRealm 域名
	 * @param headImg 图片路径
	 * @return String
	 */
	public static String getFullHeadImgUrl(String imgRealm, String headImg) {
		if (StrUtil.isNotBlank(headImg)) {
			if (!headImg.contains("http")) {
				// 非斜杠
				String virgule = "";
				if (!headImg.startsWith(VIRGULE)) {
					virgule = VIRGULE;
				}
				headImg = imgRealm + virgule + headImg;
			}
		}
		return headImg;
	}

}
