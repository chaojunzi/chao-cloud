package com.chao.cloud.common.entity;

import java.io.Serializable;

import com.chao.cloud.common.constants.ResultCodeEnum;

public class Response<T> implements Serializable {
	private String retCode = ResultCodeEnum.CODE_200.code();

	private String retMsg = "操作成功";
	private T t;

	public Response() {
	}

	public Response(boolean code) {
		if (!code) {
			this.retCode = ResultCodeEnum.CODE_500.code();
			this.retMsg = "操作失败";
		}
	}

	public Response(T t) {
		this.t = t;
	}

	public String getRetCode() {
		return this.retCode;
	}

	public void setRetCode(String retCode) {
		this.retCode = retCode;
	}

	public String getRetMsg() {
		return this.retMsg;
	}

	public void setRetMsg(String retMsg) {
		this.retMsg = retMsg;
	}

	public T getBody() {
		return this.t;
	}

	public void setBody(T t) {
		this.t = t;
	}

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

	public String toString() {
		return "Response [retCode=" + this.retCode + ", retMsg=" + this.retMsg + ", t=" + this.t + "]";
	}
}