package com.chao.cloud.common.web.crypto;

/**
 * 解密参数类型
 * 
 * @author 薛超
 * @since 2019年12月08日
 * @version 1.0.8
 */
public enum CryptoTypeEnum {
	/**
	 * 非对称加密
	 */
	RSA,
	/**
	 * 对称加密
	 */
	DES;

	/**
	 * 获取此对象
	 * 
	 * @param type 类型的String
	 * @return {@link CryptoTypeEnum}
	 */
	public static CryptoTypeEnum getByType(String type) {
		return CryptoTypeEnum.valueOf(type.toUpperCase());
	}

}
