package com.chao.cloud.common.web.sign;

/**
 * 签名算法类型
 * 
 * @author 薛超
 * @since 2019年12月08日
 * @version 1.0.8
 */
public enum SignTypeEnum {

	MD5,

	SHA1,

	SHA256;

	/**
	 * 获取此对象
	 * 
	 * @param type 类型的String
	 * @return {@link SignTypeEnum}
	 */
	public static SignTypeEnum getByType(String type) {
		return SignTypeEnum.valueOf(type.toUpperCase());
	}

}
