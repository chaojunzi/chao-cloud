package com.chao.cloud.common.entity;

/**
 * 分页参数
 * 
 * @author 薛超 功能： 时间：2018年9月12日
 * @version 1.0
 */

public abstract class BasePageVO {
	/**
	 * 当前页
	 */
	private Integer pageIndex = 1;
	/**
	 * 每页数量
	 */
	private Integer pageSize = 10;

	public Integer getPageIndex() {
		return pageIndex;
	}

	public void setPageIndex(Integer pageIndex) {
		this.pageIndex = pageIndex;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

}
