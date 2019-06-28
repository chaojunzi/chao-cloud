package com.chao.cloud.im.code.vo;

import javax.validation.constraints.NotBlank;

import lombok.Data;

/**
 * 
 * @功能：生成代码配置vo
 * @author： 薛超
 * @时间： 2019年5月27日
 * @version 1.0.0
 */
public interface GenCodeVO {

	@Data
	public static class Base {
		@NotBlank(message = "不能为空")
		private String url;
		@NotBlank(message = "不能为空")
		private String username;
		@NotBlank(message = "不能为空")
		private String password;
		@NotBlank(message = "不能为空")
		private String driverName;
		@NotBlank(message = "不能为空")
		private String parent;// package
		@NotBlank(message = "不能为空")
		private String author;// 作者
	}
}
