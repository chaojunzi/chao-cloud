package com.chao.cloud.common.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface BaseLogger {

    default Logger logger() {
        Logger logger = LoggerFactory.getLogger(getClass());
        return logger;
    };

    default Logger logger(Class clazz) {
        Logger logger = LoggerFactory.getLogger(clazz);
        return logger;
    };

    default void error(Exception e) {
        logger().error("【异常展示：Exception=-->{}】", e);
    }

    default void error(Throwable e) {
        logger().error("【异常展示：Throwable=-->{}】", e);
    }

}
