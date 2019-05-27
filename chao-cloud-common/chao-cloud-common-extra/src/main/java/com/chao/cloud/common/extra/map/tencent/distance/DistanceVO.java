package com.chao.cloud.common.extra.map.tencent.distance;

import javax.validation.constraints.NotEmpty;

import lombok.Data;

@Data
public class DistanceVO {

	private String key;
	/**
	 * 计算方式：driving（驾车）、walking（步行）
	                默认：driving
	 */
	private String mode;
	/**
	 * 起点坐标，格式：lat,lng;lat,lng... 
	            （ 经度与纬度用英文逗号分隔，坐标间用英文分号分隔）
	 */
	@NotEmpty(message = "起点不能为空：from")
	private String from;
	/**
	 * 终点坐标，格式：lat,lng;lat,lng... 
	            （经度与纬度用英文逗号分隔，坐标间用英文分号分隔）
	              注意：本服务支持单起点到多终点，或多起点到单终点，from和to参数仅可有一个为多坐标
	 */
	@NotEmpty(message = "终点不能为空：to")
	private String to;
	/**
	 * 返回格式：支持JSON/JSONP，默认JSON
	 */
	private String output;
	/**
	 * JSONP方式回调函数
	 */
	private String callback;
}
