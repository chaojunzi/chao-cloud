package com.chao.cloud.common.config.auth.annotation;

/**
 * 解析类型
 * @author 薛超
 * @since 2019年8月1日
 * @version 1.0.5
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
