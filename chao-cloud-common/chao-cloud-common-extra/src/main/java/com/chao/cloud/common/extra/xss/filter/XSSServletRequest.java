package com.chao.cloud.common.extra.xss.filter;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.lang.StringUtils;

/**
 * Xss
 * 
 * @author 薛超
 * @since 2019年11月12日
 * @version 1.0.0
 */
public class XSSServletRequest extends HttpServletRequestWrapper {
	private HttpServletRequest orgRequest = null;

	public XSSServletRequest(HttpServletRequest request) {
		super(request);
		orgRequest = request;
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
		return super.getQueryString();
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

	@Override
	public String getHeader(String name) {
		name = XssFilterUtil.stripXss(name);
		String value = super.getHeader(name);
		if (StringUtils.isNotBlank(value)) {
			value = XssFilterUtil.stripXss(value);
		}
		return value;
	}

	public HttpServletRequest getOrgRequest() {
		return orgRequest;
	}

	public static HttpServletRequest getOrgRequest(HttpServletRequest req) {
		if (req instanceof XSSServletRequest) {
			return ((XSSServletRequest) req).getOrgRequest();
		}

		return req;
	}

	private class WrappedServletInputStream extends ServletInputStream {
		public void setStream(InputStream stream) {
			this.stream = stream;
		}

		private InputStream stream;

		public WrappedServletInputStream(InputStream stream) {
			this.stream = stream;
		}

		@Override
		public int read() throws IOException {
			return stream.read();
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
