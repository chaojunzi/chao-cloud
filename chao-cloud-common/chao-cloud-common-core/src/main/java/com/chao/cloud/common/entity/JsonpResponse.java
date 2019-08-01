package com.chao.cloud.common.entity;

import lombok.Data;

/**
 * jsonp
 * @author 薛超
 * @since 2019年8月1日
 * @version 1.0.5
 */
@Data
public class JsonpResponse {

	/**
	 * 方法名
	 */
	private String jsonpFunction;
	/**
	 * 返回值
	 */
	private Object value;
}