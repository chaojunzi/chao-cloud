package com.chao.cloud.common.core;

/**
 * 签名接口
 * 
 * @author 薛超
 * @since 2021年5月26日
 * @version 1.0.0
 */
@FunctionalInterface
public interface SignFunction {
	/**
	 * 参数签名
	 * 
	 * @param source 要签名的数据
	 * @param secret 密钥 （摘要）
	 * @param priKey 私钥（非对称）
	 * @return 签名值
	 */
	String sign(String source, String secret, String priKey);

}
