package com.chao.cloud.im.dal.entity;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @功能：
 * @author： 超君子
 * @时间：2019-06-27
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ImGroupUser implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 用户id
	 */
	private Integer userId;

	/**
	 * 群id
	 */
	private Integer groupId;

}
