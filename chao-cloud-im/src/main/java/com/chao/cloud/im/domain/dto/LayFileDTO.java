package com.chao.cloud.im.domain.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LayFileDTO {
	/**
	 * 图片路径
	 */
	private String src;
	/**
	 * 图片域名
	 */
	private String domain;
	/**
	 *文件|图片名称（可选）
	 */
	private String name;
}
