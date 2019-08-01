package com.chao.cloud.common.core;

import com.chao.cloud.common.entity.Response;

/**
 * 顶级接口 :约定:为了统一修改请使用xml配置该文件
 * @author 薛超
 * @since 2019年8月1日
 * @version 1.0.5
 */
public interface IApplicationRestart {

	String SUCCESS = "SUCCESS";

	String ERROR = "ERROR";

	Response<?> restart();

}
