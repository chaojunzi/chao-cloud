package com.chao.cloud.admin.sys.domain.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LayImgDTO {
	/**
	 * 图片路径
	 */
	private String src;
	/**
	 * 图片域名
	 */
	private String domain;
	/**
	 * 图片名称（可选）
	 */
	private String title;
}
