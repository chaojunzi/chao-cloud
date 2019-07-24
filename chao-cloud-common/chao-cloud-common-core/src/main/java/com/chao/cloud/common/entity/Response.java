package com.chao.cloud.common.entity;

import java.io.Serializable;

import com.chao.cloud.common.constants.ResultCodeEnum;

import lombok.Data;

/**
 * 返回值
 * @功能：
 * @author： 薛超
 * @时间： 2019年5月27日
 * @version 1.0.0
 */
@Data
public class Response<T> implements Serializable {

	private static final long serialVersionUID = 1L;

	private String retCode = ResultCodeEnum.CODE_200.code();

	private String retMsg = "操作成功";

	private T body;

	public void setRespFailed(String retMsg) {
		this.retCode = ResultCodeEnum.CODE_500.code();
		this.retMsg = retMsg;
	}

	public void setRespFailed(String retCode, String retMsg) {
		this.retCode = retCode;
		this.retMsg = retMsg;
	}

	public void setSystemError() {
		this.retCode = ResultCodeEnum.CODE_500.code();
		this.retMsg = "操作失败";
	}

}