package com.chao.cloud.common.base;

import org.apache.commons.lang3.StringUtils;

import com.chao.cloud.common.constants.ExceptionConsts;
import com.chao.cloud.common.constants.ResultCodeEnum;

public interface BaseConvert extends BaseWirter {
    /**
     * 退出
     * @param error
     */
    default void exit(String error) {
        if (StringUtils.isBlank(error)) {
            error = ExceptionConsts.ERROR;
        }
        wirteJsonObject(ResultCodeEnum.CODE_500.code(), error);
        exit();
    }

}
