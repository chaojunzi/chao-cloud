package com.chao.cloud.common.util;

import java.math.BigInteger;
import java.util.List;

import cn.hutool.core.util.StrUtil;

/**
 * 权限计算帮助类 这个类为BigInteger封装类,基层使用BigInteger实现 主要方法:1>得到2的权的和2>测试是否具有指定编码的权限
 * 使用方法:调用方法1传入menu_id数组得到2的权的和,然后转换为字符串保存到数据库。在验证权限时调用方法2进行权限验证。
 */
public class RightsUtil {

	public static boolean checkBigInteger(String rights) {
		if (StrUtil.isBlank(rights) || "0".equals(rights)) {
			return false;
		}
		return true;
	}

	/**
	 * 得到2的权的和
	 * 
	 * @param rights
	 *            int型权限编码数组
	 * @return 2的权的和(可转换为字符保存到数据库)
	 */
	public static BigInteger sumRights(int[] rights) {
		BigInteger num = new BigInteger("0");
		for (int i = 0; i < rights.length; i++) {
			num = num.setBit(rights[i]);
		}
		return num;
	}

	/**
	 * 得到2的权的和
	 * 
	 * @param rights
	 *            int型权限编码数组
	 * @return 2的权的和(可转换为字符保存到数据库)
	 */
	public static BigInteger sumRights(List<Integer> rights) {
		BigInteger num = new BigInteger("0");
		for (Integer r : rights) {
			num = num.setBit(r);
		}
		return num;
	}

	/**
	 * 得到2的权的和
	 * 
	 * @param rights
	 *            String型权限编码数组
	 * @return 2的权的和(可转换为字符保存到数据库)
	 */
	public static BigInteger sumRights(String[] rights) {
		BigInteger num = new BigInteger("0");
		for (int i = 0; i < rights.length; i++) {
			num = num.setBit(Integer.parseInt(rights[i]));
		}
		return num;
	}

	/**
	 * 验证权限
	 * 
	 * @param sum
	 *            2的权的和
	 * @param targetRights
	 *            需要验证的数字(权限Id)
	 * @return 有权限true,无权限false
	 */
	public static boolean testRights(String sum, int targetRights) {
		if (StrUtil.isBlank(sum) || "0".equals(sum)) {
			return false;
		}
		return testRights(new BigInteger(sum), targetRights);
	}

	/**
	 * 验证权限
	 * 
	 * @param sum
	 *            2的权的和
	 * @param targetRights
	 *            需要验证的数字(权限Id)
	 * @return 有权限true,无权限false
	 */
	public static boolean testRights(String sum, String targetRights) {
		if (StrUtil.isBlank(sum) || "0".equals(sum)) {
			return false;
		}
		return testRights(new BigInteger(sum), Integer.parseInt(targetRights));
	}

	/**
	 * 验证权限(基层调用)
	 * 
	 * @param sum
	 *            2的权的和
	 * @param targetRights
	 *            需要验证的数字(权限Id)
	 * @return
	 */
	public static boolean testRights(BigInteger sum, int targetRights) {
		if (sum == null || "0".equals(sum.toString())) {
			return false;
		}
		return sum.testBit(targetRights);
	}

}