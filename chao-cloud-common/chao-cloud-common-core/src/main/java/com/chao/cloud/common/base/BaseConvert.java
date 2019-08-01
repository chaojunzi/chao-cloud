package com.chao.cloud.common.base;

import com.chao.cloud.common.constant.ExceptionConstant;
import com.chao.cloud.common.constant.ResultCodeEnum;

import cn.hutool.core.util.StrUtil;

public interface BaseConvert extends BaseWirter {
	/**
	 * 退出
	 * @param error 异常信息
	 */
	default void exit(String error) {
		if (StrUtil.isBlank(error)) {
			error = ExceptionConstant.ERROR;
		}
		wirteJsonObject(ResultCodeEnum.CODE_500.code(), error);
		exit();
	}

}
