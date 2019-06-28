package com.chao.cloud.im.domain.vo;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class MsgHisVO {

	@NotNull(message = "对方不能为空")
	private Integer id;
	@NotBlank(message = "消息类型不能为空")
	private String type;
}
