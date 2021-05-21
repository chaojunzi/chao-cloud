package com.chao.cloud.common.extra.sharding.plugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 数据库-表结构节点
 * 
 * @author 薛超
 * @since 2021年5月21日
 * @version 1.0.9
 */
@Data(staticConstructor = "of")
@Accessors(chain = true)
public class DsTableNodes {

	/**
	 * 数据源
	 */
	private String dsName;
	/**
	 * 数据表节点
	 */
	private Map<String, List<String>> tableNodes = new HashMap<>();

}
