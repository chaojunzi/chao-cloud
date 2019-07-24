package com.chao.cloud.generator.domain.vo;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

/**
 * 
 * @功能：生成代码配置vo
 * @author： 薛超
 * @时间： 2019年5月27日
 * @version 1.0.0
 */
public interface GenCodeVO {
	/**
	 * 
	 * @功能：扩展配置
	 */
	@Data
	public static class Extra {
		@NotNull
		private Integer id;// 数据库id
		@NotBlank
		private String packageName;// package包名
		@NotBlank
		private String author;// 作者
		@NotBlank
		private String style;// 风格 rest/html 两种风格

	}

	@Data
	public static class Conn {
		@NotBlank
		private String host;// 主机
		@NotNull
		private Integer port;// 端口
		@NotBlank
		private String database;// 数据库
		@NotBlank
		private String username;
		@NotBlank
		private String password;
	}
}
