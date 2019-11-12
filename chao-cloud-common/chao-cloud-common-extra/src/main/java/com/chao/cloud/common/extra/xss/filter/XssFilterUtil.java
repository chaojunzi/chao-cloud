package com.chao.cloud.common.extra.xss.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

/**
 * Xss过滤（标签|sql关键字|>转中文）
 * 
 * @author 薛超
 * @since 2019年11月12日
 * @version 1.0.8
 */
public class XssFilterUtil {

	private static List<Pattern> patterns = null;

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
		if (patterns == null) {
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
			patterns = list;
		}
		return patterns;
	}

	public static String stripXss(String value) {
		if (null == value) {
			return value;
		}
		if (StringUtils.isNotBlank(value)) {
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
		// 预防SQL盲注
		String[] pattern = { "%", "select", "insert", "delete", "from", "count\\(", "drop table", "update", "truncate",
				"asc\\(", "mid\\(", "char\\(", "xp_cmdshell", "exec", "master", "netlocalgroup administrators",
				"net user", "or", "and" };
		for (int i = 0; i < pattern.length; i++) {
			value = value.replace(pattern[i].toString(), "");
		}
		return value;
	}

	public static void main(String[] args) {
		String value = null;
		value = XssFilterUtil
				.stripXss("<br>select  ***//||&;/*-+ <>$###@%$#@$%^#$^%$&^(&*)*\\''count or %% ..... ,,,, ");
		System.out.println("type-1: '" + value + "'");

		value = XssFilterUtil.stripXss("<script src='' οnerrοr='alert(document.cookie)'></script>");
		System.out.println("type-2: '" + value + "'");

		value = XssFilterUtil.stripXss("</script>");
		System.out.println("type-3: '" + value + "'");

		value = XssFilterUtil.stripXss(" eval(abc);");
		System.out.println("type-4: '" + value + "'");

		value = XssFilterUtil.stripXss(" expression(abc);");
		System.out.println("type-5: '" + value + "'");

		value = XssFilterUtil.stripXss("<img src='' οnerrοr='alert(document.cookie);'></img>");
		System.out.println("type-6: '" + value + "'");

		value = XssFilterUtil.stripXss("<img src='' οnerrοr='alert(document.cookie);'/>");
		System.out.println("type-7: '" + value + "'");

		value = XssFilterUtil.stripXss("<img src='' οnerrοr='alert(document.cookie);'>");
		System.out.println("type-8: '" + value + "'");

		value = XssFilterUtil.stripXss("<script language=text/javascript>alert(document.cookie);");
		System.out.println("type-9: '" + value + "'");

		value = XssFilterUtil.stripXss("<script>window.location='url'");
		System.out.println("type-10: '" + value + "'");

		value = XssFilterUtil.stripXss(" οnlοad='alert(\"abc\");");
		System.out.println("type-11: '" + value + "'");

		value = XssFilterUtil.stripXss("<img src=x<!--'<\"-->>");
		System.out.println("type-12: '" + value + "'");

		value = XssFilterUtil.stripXss("<=img onstop=");
		System.out.println("type-13: '" + value + "'");
	}
}
