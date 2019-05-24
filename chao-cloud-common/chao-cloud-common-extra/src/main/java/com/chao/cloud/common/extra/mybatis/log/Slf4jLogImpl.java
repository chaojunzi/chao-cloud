package com.chao.cloud.common.extra.mybatis.log;
import org.apache.ibatis.logging.Log;
import org.slf4j.Logger;

import com.chao.cloud.common.base.BaseLogger;

/**
 * 打印sql语句
 * 
 * @author 薛超 功能： 时间：2018年9月17日
 * @version 1.0
 */
public class Slf4jLogImpl implements Log, BaseLogger {

    private Logger logger = null;

    public Slf4jLogImpl(String clazz) {
        try {
            int of = clazz.lastIndexOf(".");
            char c = clazz.charAt(of + 1);
            boolean boo = c >= 'A' && c <= 'Z' ? true : false;
            if (!boo) {
                clazz = clazz.substring(0, of);
            }
            Class<?> name = Class.forName(clazz);
            logger = logger(name);
        } catch (Exception e) {
            error(e);
            logger = logger();
        }
    }

    @Override
    public boolean isDebugEnabled() {
        return true;
    }

    @Override
    public boolean isTraceEnabled() {
        return true;
    }

    @Override
    public void error(String s, Throwable e) {
        logger.error(s);
        e.printStackTrace(System.err);
    }

    @Override
    public void error(String s) {
        logger.error(s);
    }

    @Override
    public void debug(String s) {
        // 打印sql的关键
        logger.info(s);
    }

    @Override
    public void trace(String s) {
        logger.trace(s);
    }

    @Override
    public void warn(String s) {
        logger.warn(s);
    }
}