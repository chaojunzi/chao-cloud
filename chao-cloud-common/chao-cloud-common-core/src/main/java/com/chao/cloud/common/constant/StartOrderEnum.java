package com.chao.cloud.common.constant;

/**
 * 启动顺序
 * @author 薛超
 * @since 2019年8月1日
 * @version 1.0.0
 */
public enum StartOrderEnum {

	DISPATCHER_SERVLET(0), // 控制器
	EXCEPTION(1);// 异常

	public int order;

	StartOrderEnum(Integer order) {
		this.order = order;
	}

}
