package com.chao.cloud.common.base;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public interface BaseHttpServlet extends BaseLogger {

    /**
     * 获取request
     * 
     * @return
     */
    default HttpServletRequest getRequest() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest();
        return request;
    }

    /**
     * 获取response
     * 
     * @return
     */
    default HttpServletResponse getResponse() {
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getResponse();
        return response;
    }

    /**
     * 获取请求地址
     * 
     * @param request
     * @param realm_current
     *            例如：http://www.dcdyxxx.com 不要+/ 当前域名
     * @return
     */
    default String getRequestEncodeUrl(HttpServletRequest request, String realm_current) {
        try {
            String param = "";
            String url = realm_current + request.getRequestURI();
            if (request.getQueryString() != null) {
                param = "?" + request.getQueryString();
            }
            return "?redirectUrl=" + java.net.URLEncoder.encode(url + param, "UTF-8");
        } catch (Exception e) {
        }
        return "";
    }

    /**
     * 判断方法是不是ajax
     */
    default boolean requestIsAjax(HttpServletRequest request) {
        boolean isAjax = false;
        if (!StringUtils.isBlank(request.getHeader("x-requested-with"))
                && request.getHeader("x-requested-with").equals("XMLHttpRequest")) {
            isAjax = true;
        }
        return isAjax;
    }

    default boolean isInclude(Object object) {
        return (object instanceof HttpServletRequest//
                || object instanceof HttpServletResponse//
                || object instanceof HttpSession//
        );
    }

}
