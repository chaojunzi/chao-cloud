package com.chao.cloud.common.web.crypto;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import lombok.RequiredArgsConstructor;

/**
 * 加密算法
 * 
 * @author 薛超
 * @since 2021年4月26日
 * @version 1.0.0
 */
@RequiredArgsConstructor
public enum SecretEnum {

	/**
	 * 不加密不解密
	 */
	NONE(k -> null),
	/**
	 * AES
	 */
	AES(k -> SecureUtil.aes(StrUtil.utf8Bytes(k)));

	/**
	 * 生成对称加密算法类型
	 */
	private final Function<String, SymmetricCrypto> buildSymmetricCrypto;

	private static final Map<String, SymmetricCrypto> CRYPTO_MAP = new ConcurrentHashMap<>();

	public static SecretEnum getByName(String name) {
		for (SecretEnum c : SecretEnum.values()) {
			if (StrUtil.equalsIgnoreCase(c.name(), name)) {
				return c;
			}
		}
		throw ExceptionUtil.wrapRuntime("不支持的加密算法:" + name);

	}

	/**
	 * 加密
	 * 
	 * @param data 数据
	 * @param key  密钥
	 * @return 密文
	 */
	public String encryptBase64(String data, String key) {
		if (this == NONE) {
			return data;
		}
		SymmetricCrypto symmetric = getCrypto(key);
		return symmetric.encryptBase64(data);
	}

	/**
	 * 解密
	 * 
	 * @param data 数据
	 * @param key  密钥
	 * @return 明文
	 */
	public String decryptStr(String data, String key) {
		if (this == NONE) {
			return data;
		}
		SymmetricCrypto symmetric = getCrypto(key);
		return symmetric.decryptStr(data);
	}

	private SymmetricCrypto getCrypto(String key) {
		boolean contains = CRYPTO_MAP.containsKey(key);
		if (contains) {
			return CRYPTO_MAP.get(key);
		}
		SymmetricCrypto symmetric = buildSymmetricCrypto.apply(key);
		CRYPTO_MAP.put(key, symmetric);
		return symmetric;
	}

}
