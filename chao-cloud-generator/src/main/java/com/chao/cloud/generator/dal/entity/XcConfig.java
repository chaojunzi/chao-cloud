package com.chao.cloud.generator.dal.entity;

import java.io.Serializable;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.Version;

import cn.hutool.core.date.DatePattern;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @功能：
 * @author： 超君子
 * @时间：2019-07-24
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class XcConfig implements Serializable {

	private static final long serialVersionUID = 1L;

	@TableId(value = "id", type = IdType.AUTO)
	private Integer id;

	/**
	 * 分组id
	 */
	private Integer groupId;

	/**
	 * 作者
	 */
	private String author;

	/**
	 * 包名
	 */
	private String packageName;

	/**
	 * 标题
	 */
	private String title;

	/**
	 * 主机
	 */
	private String host;

	/**
	 * 端口
	 */
	private Integer port;

	/**
	 * 数据库名称
	 */
	@TableField("`database`")
	private String database;

	/**
	 * 用户名
	 */
	private String username;

	/**
	 * 密码
	 */
	private String password;

	/**
	 * 数据库类型 1.mysql
	 */
	private Integer type;

	/**
	 * 乐观锁
	 */
	@Version
	private Integer version;

	/**
	 * 创建日期
	 */
	@DateTimeFormat(pattern = DatePattern.NORM_DATETIME_PATTERN)
	private Date createTime;

}
