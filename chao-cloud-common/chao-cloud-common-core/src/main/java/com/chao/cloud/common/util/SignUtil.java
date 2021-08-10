package com.chao.cloud.common.util;

import java.util.Map;

import com.chao.cloud.common.core.SignFunction;
import com.chao.cloud.common.core.VerifyFunction;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.exceptions.ValidateException;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.SM2;
import cn.hutool.crypto.asymmetric.Sign;
import cn.hutool.crypto.asymmetric.SignAlgorithm;
import cn.hutool.log.StaticLog;

/**
 * 签名工具类
 * 
 * @author 薛超
 * @since 2021年8月10日
 * @version 1.0.0
 */
public class SignUtil {

	/**
	 * 签名
	 * 
	 * @param paramsMap 数据
	 * @param secret    密钥
	 * @param priKey    私钥
	 * @param signFunc  签名方法
	 */
	public static String sign(Map<String, ?> paramsMap, String secret, String priKey, SignFunction signFunc) {
		// 要签名的数据
		String source = MapUtil.sortJoin(paramsMap, StrUtil.EMPTY, StrUtil.EMPTY, true);
		// 签名
		return signFunc.sign(source, secret, priKey);
	}

	/**
	 * 验签
	 * 
	 * @param paramsMap  数据
	 * @param secret     密钥
	 * @param pubKey     公钥
	 * @param verifyFunc 验签方法
	 * @param sign       验签签名值
	 */
	public static boolean verify(Map<String, ?> paramsMap, String secret, String pubKey, VerifyFunction verifyFunc,
			String sign) {
		// 要签名的数据
		String source = MapUtil.sortJoin(paramsMap, StrUtil.EMPTY, StrUtil.EMPTY, true);
		// 验签
		return verifyFunc.verify(source, secret, pubKey, sign);
	}

	/**
	 * utf8-SHA1withRSA 签名
	 * 
	 * @param data   请求数据
	 * @param priKey 私钥
	 * @return 签名值
	 */
	public static String rsaSign(String data, String priKey) {
		return rsaSign(data, priKey, CharsetUtil.UTF_8, SignAlgorithm.SHA1withRSA);
	}

	/**
	 * rsa签名
	 * 
	 * @param data      请求数据
	 * @param priKey    私钥
	 * @param charset   字符集
	 * @param algorithm 签名算法
	 * @return 签名值
	 */
	public static String rsaSign(String data, String priKey, String charset, SignAlgorithm algorithm) {
		if (StrUtil.isBlank(priKey)) {
			return StrUtil.EMPTY;
		}
		Sign sign = SecureUtil.sign(algorithm, priKey, null);
		return Base64.encode(sign.sign(StrUtil.bytes(data, charset)));
	}

	/**
	 * utf8-SHA1withRSA 验签
	 * 
	 * @param data   请求数据
	 * @param sign   签名值
	 * @param pubKey 公钥
	 * @return true or false
	 */
	public static boolean rsaVerify(String data, String sign, String pubKey) {
		return rsaVerify(data, sign, pubKey, CharsetUtil.UTF_8, SignAlgorithm.SHA1withRSA);
	}

	/**
	 * rsa 验签
	 * 
	 * @param data      请求数据
	 * @param sign      签名值
	 * @param pubKey    公钥
	 * @param charset   字符集
	 * @param algorithm 签名算法
	 * @return true or false
	 */
	public static boolean rsaVerify(String data, String sign, String pubKey, String charset, SignAlgorithm algorithm) {
		if (StrUtil.isBlank(pubKey)) {
			return false;
		}
		Sign verify = SecureUtil.sign(algorithm, null, pubKey);
		return verify.verify(StrUtil.bytes(data, charset), Base64.decode(sign));
	}

	/**
	 * 国密算法
	 */
	public static String sm2Sign(String data, String priKey) {
		SM2 sm2 = null;
		try {
			sm2 = new SM2(priKey, null);
		} catch (Exception e) {
			StaticLog.error(e);
			throw new ValidateException("【SM2】初始化失败:无效的priKey={}", priKey);
		}
		return sm2.signHex(HexUtil.encodeHexStr(data));
	}

	public static boolean sm2Verify(String data, String sign, String pubKey) {
		SM2 sm2 = null;
		try {
			sm2 = new SM2(null, pubKey);
		} catch (Exception e) {
			StaticLog.error(e);
			throw new ValidateException("【SM2】初始化失败:无效的pubKey={}", pubKey);
		}
		return sm2.verifyHex(HexUtil.encodeHexStr(data), sign);
	}
}
