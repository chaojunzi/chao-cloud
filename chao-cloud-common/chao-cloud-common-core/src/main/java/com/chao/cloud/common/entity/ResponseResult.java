package com.chao.cloud.common.entity;

import com.chao.cloud.common.constants.ResultCodeEnum;

public class ResponseResult {
	/**
	 * 成功
	 * @return
	 */
	public static Response<String> ok() {
		return ok("ok");
	}

	/**
	 * 获取一般的对象
	 * 
	 * @param type
	 * @return
	 */
	public static <T> Response<T> ok(T t) {
		Response<T> response = new Response<T>();
		response.setBody(t);
		return response;
	}

	/**
	 * 获取响应码和提示
	 * @param type
	 * @return
	 */
	public static <T> Response<T> result(String code, String msg) {
		Response<T> response = new Response<T>();
		response.setRespFailed(code, msg);
		return response;
	}

	/**
	 * 失败
	 * @return
	 */
	public static Response<String> error() {
		return error("error");
	}

	public static <T> Response<T> error(String msg) {
		Response<T> response = new Response<T>();
		response.setRespFailed(ResultCodeEnum.CODE_500.code(), msg);
		return response;
	}

	public static <T> Response<T> error(Class<T> clazz, String failed) {
		Response<T> response = new Response<T>();
		response.setRespFailed(ResultCodeEnum.CODE_500.code(), failed);
		return response;
	}

	/**
	 * 废弃**************************************************
	 * @param <T>
	 * @param t
	 * @return
	 */
	@Deprecated
	public static <T> Response<T> getResponseResultError(T t) {
		Response<T> response = new Response<T>();
		response.setRespFailed(ResultCodeEnum.CODE_500.code(), "失败");
		response.setBody(t);
		return response;
	}

	@Deprecated
	public static <T> Response<T> getResponseResultError(Class<T> clazz, String failed) {
		Response<T> response = new Response<T>();
		response.setRespFailed(ResultCodeEnum.CODE_500.code(), failed);
		return response;
	}

	@Deprecated
	public static <T> Response<T> getResponseCodeAndMsg(String code, String msg) {
		Response<T> response = new Response<T>();
		response.setRespFailed(code, msg);
		return response;
	}

	/**
	 * 获取一般的对象
	 * 
	 * @param type
	 * @return
	 */
	@Deprecated
	public static <T> Response<T> getResponseResult(T t) {
		Response<T> response = new Response<T>();
		response.setBody(t);
		return response;
	}
}
