package com.chao.cloud.common.base;

import com.chao.cloud.common.constants.ExceptionConsts;
import com.chao.cloud.common.constants.ResultCodeEnum;

import cn.hutool.core.util.StrUtil;

public interface BaseConvert extends BaseWirter {
	/**
	 * 退出
	 * @param error
	 */
	default void exit(String error) {
		if (StrUtil.isBlank(error)) {
			error = ExceptionConsts.ERROR;
		}
		wirteJsonObject(ResultCodeEnum.CODE_500.code(), error);
		exit();
	}

}
