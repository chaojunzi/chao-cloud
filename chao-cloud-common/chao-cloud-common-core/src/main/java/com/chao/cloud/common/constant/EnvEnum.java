package com.chao.cloud.common.constant;

import com.chao.cloud.common.exception.BusinessException;

/**
 * 环境变量
 * @author： 薛超
 * @since：2018年12月13日
 * @version 2.0
 */
public enum EnvEnum {
	/**
	 * 生产环境
	 */
	PROD(true),
	/**
	 * 开发环境
	 */
	DEV(false),
	/**
	 * 测试环境
	 */
	TEST(false);

	private Boolean status;

	EnvEnum(Boolean status) {
		this.status = status;
	}

	public static boolean statusByEnv(String env) {
		for (EnvEnum profile : EnvEnum.values()) {
			if (profile.name().toLowerCase().equals(env.toLowerCase())) {
				return profile.status;
			}
		}
		throw new BusinessException("无效的环境系统变量profile=" + env);
	}
}
