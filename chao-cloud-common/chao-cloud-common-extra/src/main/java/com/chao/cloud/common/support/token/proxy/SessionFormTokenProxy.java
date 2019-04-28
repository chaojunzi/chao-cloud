package com.chao.cloud.common.support.token.proxy;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import com.chao.cloud.common.base.BaseProxy;
import com.chao.cloud.common.exception.BusinessException;
import com.chao.cloud.common.support.token.annotation.FormToken;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * token 拦截（默认为session实现）
 * @功能：
 * @author： 薛超
 * @时间：2019年3月18日
 * @version 1.0.0
 */
@Aspect
@Slf4j
public class SessionFormTokenProxy implements BaseProxy {

    private static final String FROM_TOKEN_KEY = "formToken";

    // 环绕拦截
    @Around(value = "@annotation(com.chao.cloud.common.support.token.annotation.FormToken)")
    public Object around(ProceedingJoinPoint pdj) throws Exception {
        Object obj = null;
        HttpServletRequest request = getRequest();
        log.info("[token]----start----------");
        Method method = getMethod(pdj);
        FormToken annotation = method.getAnnotation(FormToken.class);
        if (annotation != null) {
            boolean needSaveSession = annotation.save();
            // 添加token
            if (needSaveSession) {
                request.getSession(true).setAttribute(FROM_TOKEN_KEY, IdUtil.fastUUID());
            }
            boolean needRemoveSession = annotation.remove();
            // 删除token
            if (needRemoveSession) {
                boolean isRepeat = isRepeatSubmit(request);
                request.getSession(true).removeAttribute(FROM_TOKEN_KEY);
                if (isRepeat) {
                    log.warn("请不要重复提交,url:{}", request.getServletPath());
                    throw new BusinessException("请不要重复提交");

                }
            }
        }
        // 执行
        try {
            obj = pdj.proceed();
        } catch (Throwable e) {
            throw new BusinessException(ExceptionUtil.getMessage(e));
        }
        log.info("[token]------end----------");
        return obj;
    }

    /**
     * 判断是否重复提交表单.
     *
     * @param request the request
     * @return the boolean
     */
    private boolean isRepeatSubmit(HttpServletRequest request) {
        String serverToken = (String) request.getSession(true).getAttribute(FROM_TOKEN_KEY);
        if (StrUtil.isEmpty(serverToken)) {
            return true;
        }
        String clinetToken = request.getParameter(FROM_TOKEN_KEY);
        if (StrUtil.isEmpty(clinetToken)) {
            return true;
        }
        if (!serverToken.equals(clinetToken)) {
            return true;
        }
        return false;
    }

}