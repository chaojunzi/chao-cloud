package com.chao.cloud.common.util;

import java.math.BigDecimal;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;

/**
 * 数字工具类
 * 
 * @author 薛超
 * @since 2021年6月30日
 * @version 1.0.0
 */
public class NumUtil {
	/**
	 * #号
	 */
	public static final String POUND = "#";

	public static final char ZERO = '0';

	/**
	 * 数字转{@link BigDecimal}<br>
	 * null或""或空白符转换为null
	 *
	 * @param number 数字字符串
	 * @return {@link BigDecimal}
	 */
	public static BigDecimal toBigDecimal(String number) {
		if (NumberUtil.isNumber(number)) {
			return NumberUtil.toBigDecimal(number);
		}
		return null;
	}

	/**
	 * 数字转字符串<br>
	 * 调用{@link Number#toString()}或 {@link BigDecimal#toPlainString()}，并去除尾小数点儿后多余的0
	 *
	 * @param number A Number
	 * @return A String.
	 */
	public static String toStr(Number number) {
		if (number == null) {
			return StrUtil.EMPTY;
		}
		return NumberUtil.toStr(number);
	}

	/**
	 * 数字转字符串 并保留小数位
	 * 
	 * @param number 数字
	 * @param scale  小数位
	 * @return 数字
	 */
	public static String formatNumber(Number num, int scale) {
		String pattern = NumUtil.POUND;
		if (scale > 0) {
			pattern = StrUtil.format("0.{}", StrUtil.repeat(NumUtil.ZERO, scale));
		}
		return NumberUtil.decimalFormat(pattern, num);
	}

	public static BigDecimal round(BigDecimal number, int scale) {
		if (number == null) {
			return null;
		}
		//
		String numStr = NumberUtil.toStr(number);
		int decimalLen = StrUtil.subAfter(numStr, StrUtil.DOT, true).length();
		if (decimalLen < scale) {
			// 补零
			return NumberUtil.round(number.doubleValue(), scale);
		}
		return NumberUtil.toBigDecimal(numStr);
	}

	/**
	 * 删除数字类型字符串 -> 前面的0
	 * 
	 * @param num
	 * @return 数字字符串
	 */
	public static String delZeroPre(String num) {
		if (!NumberUtil.isNumber(num)) {
			return num;
		}
		return NumberUtil.toStr(NumberUtil.parseNumber(num));
	}

}
