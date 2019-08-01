package com.chao.cloud.common.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 树形接口-递归算法
 * @author 薛超
 * @since 2019年8月1日
 * @version 1.0.5
 */
public interface TreeEntity<E> {
	/**
	 * id
	 * @return {@link Serializable}
	 */
	Serializable getId();

	/**
	 * 父id
	 * @return {@link Serializable}
	 */
	Serializable getParentId();

	/**
	 * 子集
	 * @param subList 子集List
	 */
	void setSubList(List<E> subList);
}