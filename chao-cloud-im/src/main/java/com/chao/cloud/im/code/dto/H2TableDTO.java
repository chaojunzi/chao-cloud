package com.chao.cloud.im.code.dto;

import com.chao.cloud.im.code.core.IDatabaseInfo;

import lombok.Data;

@Data
public class H2TableDTO implements IDatabaseInfo{

	private String tableSchema;
	private String tableName;
 
}
