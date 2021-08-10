package com.chao.cloud.common.core;

/**
 * 验签接口
 * 
 * @author 薛超
 * @since 2021年5月26日
 * @version 1.0.0
 */
@FunctionalInterface
public interface VerifyFunction {
	/**
	 * 结果验签
	 * 
	 * @param source 要验签的数据
	 * @param secret 密钥 （摘要）
	 * @param pubKey 公钥（非对称）
	 * @param sign   要验证的签名
	 * @return true or false
	 */
	boolean verify(String source, String secret, String pubKey, String sign);

}
