package com.chao.cloud.generator.domain.vo;

import javax.validation.constraints.NotNull;

import lombok.Data;

/**
 * 
 * @功能：连接
 * @author： 薛超
 * @时间： 2019年7月24日
 * @version 1.0.0
 */
@Data
public class ConnVO {

	@NotNull
	private Integer id;

	private String tableName;

}
