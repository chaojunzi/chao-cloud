package com.chao.cloud.common.extra.xss.filter;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.URLUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * Xss 过滤 封装HttpServletRequestWrapper
 * 
 * @author 薛超
 * @since 2019年11月12日
 * @version 1.0.8
 */
@Slf4j
public class XSSServletRequest extends HttpServletRequestWrapper {
	private HttpServletRequest orgRequest = null;

	private Map<String, String[]> params = new HashMap<String, String[]>();

	public XSSServletRequest(HttpServletRequest request) {
		super(request);
		orgRequest = request;
	}

	@Override
	public Map<String, String[]> getParameterMap() {
		// 此map无法修改
		Map<String, String[]> paramMap = super.getParameterMap();
		if (MapUtils.isNotEmpty(paramMap)) {
			Set<Entry<String, String[]>> entrySet = paramMap.entrySet();
			for (Entry<String, String[]> entry : entrySet) {
				String key = entry.getKey();
				// 此values不可直接修改
				String[] values = entry.getValue();
				String[] v = null;
				if (ArrayUtil.isNotEmpty(values)) {
					v = new String[ArrayUtil.length(values)];
					for (int i = 0; i < values.length; i++) {
						if (values[i] instanceof String) {
							v[i] = XssFilterUtil.stripXss(values[i]);// 包含sql注入
						}
					}
				}
				params.put(XssFilterUtil.clearXss(key), v);// key可以包含sql关键字
			}
		}
		paramMap = params;
		return paramMap;
	}

	@Override
	public String getParameter(String name) {
		String string = super.getParameter(name);
		// 返回值之前 先进行过滤
		return XssFilterUtil.stripXss(string);
	}

	@Override
	public String[] getParameterValues(String name) {
		// 返回值之前 先进行过滤
		String[] values = super.getParameterValues(name);
		if (values != null) {
			for (int i = 0; i < values.length; i++) {
				values[i] = XssFilterUtil.stripXss(values[i]);
			}
		}
		return values;
	}

	@Override
	public String getQueryString() {
		String queryString = super.getQueryString();
		if (StringUtils.isNotBlank(queryString)) {
			try {// 路径转码
				String pathDecode = URLUtil.decode(queryString);
				return XssFilterUtil.stripXss(pathDecode);
			} catch (Exception e) {
				log.error("转码失败：" + queryString, e);
			}
		}
		return queryString;
	}

	@Override
	public ServletInputStream getInputStream() throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(orgRequest.getInputStream()));
		String line = br.readLine();
		String result = "";
		if (line != null) {
			result += XssFilterUtil.stripXss(line);
		}
		return new WrappedServletInputStream(new ByteArrayInputStream(result.getBytes()));
	}

	/**
	 * 覆盖getHeader方法，将参数名和参数值都做xss过滤。<br>
	 * 如果需要获得原始的值，则通过super.getHeaders(name)来获取<br>
	 * getHeaderNames 也可能需要覆盖
	 * 
	 * @param name 请求头key
	 * @return 请求头信息
	 */
	@Override
	public String getHeader(String name) {
		name = XssFilterUtil.stripXss(name);
		String value = super.getHeader(name);
		if (StringUtils.isNotBlank(value)) {
			value = XssFilterUtil.stripXss(value);
		}
		return value;
	}

	/**
	 * 获取最原始的request
	 * 
	 * @return {@link HttpServletRequest}
	 */
	public HttpServletRequest getOrgRequest() {
		return orgRequest;
	}

	/**
	 * 获取最原始的request的静态方法
	 * 
	 * @param request {@link HttpServletRequest}
	 * @return {@link HttpServletRequest}
	 */
	public static HttpServletRequest getOrgRequest(HttpServletRequest request) {
		if (request instanceof XSSServletRequest) {
			return ((XSSServletRequest) request).getOrgRequest();
		}

		return request;
	}

	private class WrappedServletInputStream extends ServletInputStream {
		public void setStream(InputStream in) {
			this.in = in;
		}

		private InputStream in;

		public WrappedServletInputStream(InputStream in) {
			this.in = in;
		}

		@Override
		public int read() throws IOException {
			return in.read();
		}

		@Override
		public boolean isFinished() {
			return true;
		}

		@Override
		public boolean isReady() {
			return true;
		}

		@Override
		public void setReadListener(ReadListener readListener) {

		}
	}
}
