package com.chao.cloud.common.base;

import cn.hutool.core.util.StrUtil;

public interface BaseStringOptional {
	// 斜杠
	String VIRGULE = "/";

	public static String getFullHeadImgUrl(String imgDomain, String headImg) {
		if (StrUtil.isNotBlank(headImg)) {
			if (!headImg.contains("http")) {
				// 非斜杠
				String virgule = "";
				if (!headImg.startsWith(VIRGULE)) {
					virgule = VIRGULE;
				}
				headImg = imgDomain + virgule + headImg;
			}
		}
		return headImg;
	}

}
