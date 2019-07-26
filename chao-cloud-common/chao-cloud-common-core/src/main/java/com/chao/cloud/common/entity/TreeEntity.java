package com.chao.cloud.common.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * @功能：树形接口-递归算法
 * @author： 薛超
 * @时间： 2019年7月26日
 * @version 1.0.0
 */
public interface TreeEntity<E> {
	/**
	 * id
	 * @return
	 */
	Serializable getId();

	/**
	 * 父id
	 * @return
	 */
	Serializable getParentId();

	/**
	 * 子集
	 * @param subList
	 */
	void setSubList(List<E> subList);
}