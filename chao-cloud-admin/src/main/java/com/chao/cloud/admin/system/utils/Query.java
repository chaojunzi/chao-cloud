package com.chao.cloud.admin.system.utils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 
 * @功能：查询参数
 * @author： 薛超
 * @时间： 2019年5月27日
 * @version 1.0.0
 */
@Deprecated
public class Query extends LinkedHashMap<String, Object> {
	private static final long serialVersionUID = 1L;
	//
	private int offset;
	// 每页条数
	private int limit;

	public Query(Map<String, Object> params) {
		this.putAll(params);
		// 分页参数
		this.limit = Integer.parseInt(params.get("limit").toString());
		int page = Integer.parseInt(params.get("page").toString());
		this.put("offset", (page - 1) * limit);
		this.put("limit", limit);
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.put("offset", offset);
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}
}
