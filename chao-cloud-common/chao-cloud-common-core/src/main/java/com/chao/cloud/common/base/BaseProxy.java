package com.chao.cloud.common.base;

import java.lang.reflect.Method;
import java.util.stream.Stream;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;

import cn.hutool.core.util.ArrayUtil;

public interface BaseProxy extends BaseLogger, BaseHttpServlet {

    /**
     * 环绕拦截
     * 
     * @param pdj
     * @return
     */
    Object around(ProceedingJoinPoint pdj) throws Throwable;

    /**
     * 获取方法
     */
    default Method getMethod(ProceedingJoinPoint pdj) {
        Signature signature = pdj.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();
        return method;
    }

    /**
     * 获取参数
     */
    default <T> T getParamFirst(Class<T> type, Object[] args) {
        boolean empty = ArrayUtil.isEmpty(args);
        if (empty) {
            return null;
        }
        return Stream.of(args).filter(arg -> type.isInstance(arg)).findFirst().map(arg -> (T) arg).orElse(null);

    }

    default <T> T getParamByName(Class<T> type, String name, ProceedingJoinPoint pdj) {
        LocalVariableTableParameterNameDiscoverer u = new LocalVariableTableParameterNameDiscoverer();
        Method method = getMethod(pdj);
        String[] parameterNames = u.getParameterNames(method);
        for (int i = 0; i < parameterNames.length; i++) {
            if (name.equals(parameterNames[i])) {
                return (T) pdj.getArgs()[i];
            }
        }
        return null;
    }

}
