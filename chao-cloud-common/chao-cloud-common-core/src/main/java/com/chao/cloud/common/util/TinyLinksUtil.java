package com.chao.cloud.common.util;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.digest.DigestUtil;

/**
 * 短链接工具
 * 
 * @author 薛超
 * @since 2020年5月28日
 * @version 1.0.9
 */
public class TinyLinksUtil {

	// 要使用生成 URL 的字符
	private static final String[] CHARS = new String[] { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l",
			"m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5", "6",
			"7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R",
			"S", "T", "U", "V", "W", "X", "Y", "Z" };

	/**
	 * 生成短链接地址
	 * 
	 * @param url 网址
	 * @param key md5加密的Key
	 * @return 短链接字符串
	 */
	public static String shortUrl(String url, String key) {
		String hex = DigestUtil.md5Hex(key + url).toUpperCase(); // 对传入网址进行 MD5 加密，key是加密字符串
		// 把加密字符按照8位一组16进制与0x3FFFFFFF进行位与运算
		int i = RandomUtil.randomInt(4);
		String subStr = hex.substring(i * 8, i * 8 + 8);
		// 这里需要使用 long 型来转换，因为 Inteter.parseInt() 只能处理 31 位 , 首位为符号位 , 如果不用 long ，则会越界
		long loopHex = 0x3FFFFFFF & Long.parseLong(subStr, 16);
		StringBuilder outChars = new StringBuilder();
		for (int j = 0; j < 6; j++) {
			long index = 0x0000003D & loopHex; // 把得到的值与 0x0000003D 进行位与运算，取得字符数组 chars 索引
			outChars.append(CHARS[(int) index]); // 把取得的字符相加
			loopHex = loopHex >> 5; // 每次循环按位右移 5 位
		}
		return outChars.toString();
	}
}
