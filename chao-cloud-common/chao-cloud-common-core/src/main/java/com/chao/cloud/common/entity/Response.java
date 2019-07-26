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

	private String retCode;

	private String retMsg;

	private T body;

	/**
	 * ***************静态方法*******************
	 */
	/**
	 * 成功
	 * @return
	 */
	public static Response<String> ok() {
		return ok(null);
	}

	public static <T> Response<T> ok(T body) {
		return ok(body, "ok");
	}

	public static <T> Response<T> ok(T body, String msg) {
		return result(body, ResultCodeEnum.CODE_200.code(), msg);
	}

	/**
	 * 失败
	 * @return
	 */
	public static Response<String> error() {
		return error("error");
	}

	public static <T> Response<T> error(String msg) {
		return result(ResultCodeEnum.CODE_500.code(), msg);
	}

	public static <T> Response<T> error(Class<T> clazz, String msg) {
		return result(ResultCodeEnum.CODE_500.code(), msg);
	}

	/**
	 * 返回
	 */
	public static <T> Response<T> result(T body, String retCode, String retMsg) {
		Response<T> apiResult = new Response<>();
		apiResult.setBody(body);
		apiResult.setRetCode(retCode);
		apiResult.setRetMsg(retMsg);
		return apiResult;
	}

	public static <T> Response<T> result(String code, String msg) {
		return result(null, code, msg);
	}
}