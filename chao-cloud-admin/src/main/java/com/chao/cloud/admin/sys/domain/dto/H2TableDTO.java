package com.chao.cloud.admin.sys.domain.dto;

import com.chao.cloud.admin.sys.constant.IDatabaseInfo;

import lombok.Data;

@Data
public class H2TableDTO implements IDatabaseInfo{

	private String tableSchema;
	private String tableName;

}
