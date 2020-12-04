package com.chao.cloud.common.extra.sharding.convert;

import lombok.Data;

/**
 * 分片对象
 * 
 * @author 薛超
 * @since 2020年12月2日
 * @version 1.0.0
 */
public interface ShardingModel {

	void setCode(String code);

	void setShardingCode(String shardingCode);

	String getCode();

	String getShardingCode();

	@Data
	public static class ShardingJson implements ShardingModel {
		/**
		 * 组织机构代码
		 */
		private String code;
		/**
		 * 分片标识
		 */
		private String shardingCode;
	}

}
