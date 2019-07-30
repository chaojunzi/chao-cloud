package com.chao.cloud.common.config.auth.annotation;

/**
 * @author 薛超
 * 转换类型
 * 功能：
 * 时间：2018年7月26日
 * @version 3.0
 */
public enum ResolverEnum {
	HEADER(1), PARAM(2);

	public Integer type;

	ResolverEnum(Integer type) {
		this.type = type;
	}

	public static ResolverEnum getByType(Integer type) {
		for (ResolverEnum typeEnum : ResolverEnum.values()) {
			if (typeEnum.type == type) {
				return typeEnum;
			}
		}
		return null;
	}

}
