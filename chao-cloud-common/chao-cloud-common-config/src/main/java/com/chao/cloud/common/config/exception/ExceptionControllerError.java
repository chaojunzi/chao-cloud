package com.chao.cloud.common.config.exception;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import com.chao.cloud.common.base.BaseHttpServlet;
import com.chao.cloud.common.constants.ResultCodeEnum;
import com.chao.cloud.common.entity.Response;
import com.chao.cloud.common.entity.ResponseResult;

/**
 * Springboot全局异常统一处理
 * @功能：
 * @author： 薛超
 * @时间：2019年3月19日
 * @version 1.0.0
 */
@RestController
@EnableConfigurationProperties({ ServerProperties.class })
public class ExceptionControllerError implements ErrorController, BaseHttpServlet {

	private static final String ERROR_PATH = "/error";

	private ErrorAttributes errorAttributes;

	@Autowired
	private ServerProperties serverProperties;

	/**
	 * 初始化ExceptionController
	 * 
	 * @param errorAttributes
	 */
	@Autowired
	public ExceptionControllerError(ErrorAttributes errorAttributes) {
		Assert.notNull(errorAttributes, "ErrorAttributes must not be null");
		this.errorAttributes = errorAttributes;
	}

	@RequestMapping(value = ERROR_PATH)
	public Response<Object> error(HttpServletRequest request) {
		Map<String, Object> body = getErrorAttributes(request, isIncludeStackTrace(request, MediaType.ALL));
		HttpStatus status = getStatus(request);
		if (requestIsAjax(request)) {
			Response<Object> response = ResponseResult.getResponseCodeAndMsg(ResultCodeEnum.CODE_500.code(),
					body.get("message").toString());
			response.setBody(status);
			return response;
		}
		// 返回错误页面
		HttpServletResponse response = getResponse();
		ErrorUtil.writeErrorHtml(response);
		return null;
	}

	/**
	 * Determine if the stacktrace attribute should be included.
	 * 
	 * @param request
	 *            the source request
	 * @param produces
	 *            the media type produced (or {@code MediaType.ALL})
	 * @return if the stacktrace attribute should be included
	 */
	protected boolean isIncludeStackTrace(HttpServletRequest request, MediaType produces) {
		ErrorProperties.IncludeStacktrace include = this.serverProperties.getError().getIncludeStacktrace();
		if (include == ErrorProperties.IncludeStacktrace.ALWAYS) {
			return true;
		}
		if (include == ErrorProperties.IncludeStacktrace.ON_TRACE_PARAM) {
			return getTraceParameter(request);
		}
		return false;
	}

	/**
	 * 获取错误的信息
	 * 
	 * @param request
	 * @param includeStackTrace
	 * @return
	 */
	private Map<String, Object> getErrorAttributes(HttpServletRequest request, boolean includeStackTrace) {
		WebRequest requestAttributes = new ServletWebRequest(request);
		return this.errorAttributes.getErrorAttributes(requestAttributes, includeStackTrace);
	}

	/**
	 * 是否包含trace
	 * 
	 * @param request
	 * @return
	 */
	private boolean getTraceParameter(HttpServletRequest request) {
		String parameter = request.getParameter("trace");
		if (parameter == null) {
			return false;
		}
		return !"false".equals(parameter.toLowerCase());
	}

	/**
	 * 获取错误编码
	 * 
	 * @param request
	 * @return
	 */
	private HttpStatus getStatus(HttpServletRequest request) {
		Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
		if (statusCode == null) {
			return HttpStatus.INTERNAL_SERVER_ERROR;
		}
		try {
			return HttpStatus.valueOf(statusCode);
		} catch (Exception ex) {
			return HttpStatus.INTERNAL_SERVER_ERROR;
		}
	}

	/**
	 * 实现错误路径,暂时无用
	 * 
	 * @return
	 */
	@Override
	public String getErrorPath() {
		return ERROR_PATH;
	}

}
