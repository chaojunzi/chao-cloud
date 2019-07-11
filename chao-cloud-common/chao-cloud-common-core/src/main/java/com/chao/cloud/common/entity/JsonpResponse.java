package com.chao.cloud.common.entity;

import lombok.Data;

/**
 * 
 * @功能：jsonp
 * @author： 薛超
 * @时间： 2019年7月11日
 * @version 1.0.0
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