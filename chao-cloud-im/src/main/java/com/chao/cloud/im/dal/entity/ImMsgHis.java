package com.chao.cloud.im.dal.entity;

import java.io.Serializable;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.Version;

import cn.hutool.core.date.DatePattern;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @功能：
 * @author： 超君子
 * @时间：2019-06-28
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ImMsgHis implements Serializable {

	private static final long serialVersionUID = 1L;

	@TableId(value = "msg_id", type = IdType.AUTO)
	private Integer msgId;

	/** 
	 * 发送人->接收人 如 1-2
	 */
	private String fromTo;

	/**
	 * group=群id    friend=发送者id
	 */
	private Integer id;

	/**
	 * 用户名
	 */
	private String username;

	/**
	 * 头像
	 */
	private String avatar;

	/**
	 * 消息内容
	 */
	private String content;

	/**
	 * 类型   group friend
	 */
	private String type;

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

	public Long getTimestamp() {
		return createTime.getTime();
	}
}
