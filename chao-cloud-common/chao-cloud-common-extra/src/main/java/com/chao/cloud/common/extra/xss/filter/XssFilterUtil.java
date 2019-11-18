package com.chao.cloud.common.extra.xss.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.hutool.core.util.StrUtil;

/**
 * Xss过滤（标签|sql关键字|>转中文）
 * 
 * @author 薛超
 * @since 2019年11月12日
 * @version 1.0.8
 */
public class XssFilterUtil {
	// sql 关键字
	private static String[] SQL_PATTERNS = { "%", "select", "insert", "delete", "from", "count\\(", "drop table",
			"update", "truncate", "asc\\(", "mid\\(", "char\\(", "xp_cmdshell", "exec", "master",
			"netlocalgroup administrators", "net user", "or", "and" };
	// 网页html
	private static List<Pattern> HTML_PATTERNS = null;

	private static List<Object[]> getXssPatternList() {
		List<Object[]> ret = new ArrayList<Object[]>();

		ret.add(new Object[] { "<(no)?script[^>]*>.*?</(no)?script>", Pattern.CASE_INSENSITIVE });
		ret.add(new Object[] { "eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL });
		ret.add(new Object[] { "expression\\((.*?)\\)",
				Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL });
		ret.add(new Object[] { "(javascript:|vbscript:|view-source:)*", Pattern.CASE_INSENSITIVE });
		ret.add(new Object[] { "<(\"[^\"]*\"|\'[^\']*\'|[^\'\">])*>",
				Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL });
		ret.add(new Object[] {
				"(window\\.location|window\\.|\\.location|document\\.cookie|document\\.|alert\\(.*?\\)|window\\.open\\()*",
				Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL });
		ret.add(new Object[] {
				"<+\\s*\\w*\\s*(oncontrolselect|oncopy|oncut|ondataavailable|ondatasetchanged|ondatasetcomplete|ondblclick|ondeactivate|ondrag|ondragend|ondragenter|ondragleave|ondragover|ondragstart|ondrop|οnerrοr=|onerroupdate|onfilterchange|onfinish|onfocus|onfocusin|onfocusout|onhelp|onkeydown|onkeypress|onkeyup|onlayoutcomplete|onload|onlosecapture|onmousedown|onmouseenter|onmouseleave|onmousemove|onmousout|onmouseover|onmouseup|onmousewheel|onmove|onmoveend|onmovestart|onabort|onactivate|onafterprint|onafterupdate|onbefore|onbeforeactivate|onbeforecopy|onbeforecut|onbeforedeactivate|onbeforeeditocus|onbeforepaste|onbeforeprint|onbeforeunload|onbeforeupdate|onblur|onbounce|oncellchange|onchange|onclick|oncontextmenu|onpaste|onpropertychange|onreadystatechange|onreset|onresize|onresizend|onresizestart|onrowenter|onrowexit|onrowsdelete|onrowsinserted|onscroll|onselect|onselectionchange|onselectstart|onstart|onstop|onsubmit|onunload)+\\s*=+",
				Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL });
		return ret;
	}

	private static List<Pattern> getPatterns() {
		if (HTML_PATTERNS == null) {
			List<Pattern> list = new ArrayList<Pattern>();
			String regex = null;
			Integer flag = null;
			int arrLength = 0;
			for (Object[] arr : getXssPatternList()) {
				arrLength = arr.length;
				for (int i = 0; i < arrLength; i++) {
					regex = (String) arr[0];
					flag = (Integer) arr[1];
					list.add(Pattern.compile(regex, flag));
				}
			}
			HTML_PATTERNS = list;
		}
		return HTML_PATTERNS;
	}

	/**
	 * 清理xss(不包含sql)
	 * 
	 * @param value 参数
	 * @return value
	 */
	public static String clearXss(String value) {
		if (StrUtil.isNotBlank(value)) {
			Matcher matcher = null;
			for (Pattern pattern : getPatterns()) {
				matcher = pattern.matcher(value);
				// 匹配
				if (matcher.find()) {
					// 删除相关字符串
					value = matcher.replaceAll("");
				}
			}
			value = value.replaceAll("<", "＜").replaceAll(">", "＞");
			value = value.replaceAll("\\(", "（").replaceAll("\\)", "）");
			value = value.replaceAll("'", "＇").replaceAll("\"", "＂");
			// value = HtmlUtils.htmlEscape(value);
		}
		return value;
	}

	/**
	 * 清理xss(包含sql)
	 * 
	 * @param value 参数
	 * @return value
	 */
	public static String stripXss(String value) {
		// 清理Xss
		value = clearXss(value);
		// 预防SQL盲注
		value = stripSql(value);
		return value;
	}

	/**
	 * sql注入的问题
	 * 
	 * @param value 参数
	 * @return value
	 */
	public static String stripSql(String value) {
		if (StrUtil.isNotBlank(value)) {
			// 预防SQL盲注
			for (int i = 0; i < SQL_PATTERNS.length; i++) {
				value = value.replace(SQL_PATTERNS[i], "");
			}
		}
		return value;
	}

}
