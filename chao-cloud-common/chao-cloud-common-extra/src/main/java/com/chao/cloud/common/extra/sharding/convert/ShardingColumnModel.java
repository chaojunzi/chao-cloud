package com.chao.cloud.common.extra.sharding.convert;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 组织机构模型
 * 
 * @author 薛超
 * @since 2021年6月21日
 * @version 1.0.0
 */
@Data(staticConstructor = "of")
@Accessors(chain = true)
public class ShardingColumnModel {
	/**
	 * 部门表中开票网点编码
	 */
	private String orgCode;
	/**
	 * 部门表中组织机构名称
	 */
	private String orgName;
	/**
	 * 部门表中组织机构代码
	 */
	private String shardingCode;
	/**
	 * 部门是否存在
	 */
	private boolean orgExist;
}
