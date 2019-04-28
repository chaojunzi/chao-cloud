package com.chao.cloud.common.constants;

/**
 * 启动顺序
 * @author 薛超
 * 功能：
 * 时间：2018年7月23日
 */
public enum StartOrderEnum {

	DISPATCHER_SERVLET(0), // 控制器
	EXCEPTION(1);// 异常

	public int order;

	StartOrderEnum(Integer order) {
		this.order = order;
	}

}
