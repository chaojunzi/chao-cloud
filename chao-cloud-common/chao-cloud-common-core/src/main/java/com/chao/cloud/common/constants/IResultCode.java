package com.chao.cloud.common.constants;

/**
 * 返回值接口
 * 
 * @author 薛超 功能： 时间：2018年9月5日
 * @version 1.0
 */
public interface IResultCode {
	/**
	 * 服务异常-如空接口
	 * 
	 * @return
	 */
	default String CODE_1() {
		return ResultCodeEnum.CODE_1.code;
	}

	/**
	 * 成功
	 * 
	 * @return
	 */
	default String CODE_200() {
		return ResultCodeEnum.CODE_200.code;
	}

	/**
	 * 业务异常-需要自己处理
	 * 
	 * @return
	 */
	default String CODE_500() {
		return ResultCodeEnum.CODE_500.code;
	}

	/**
	 * 权限不足
	 * 
	 * @return
	 */
	default String CODE_403() {
		return ResultCodeEnum.CODE_403.code;
	}

	/**
	 * 远程业务异常-执行中
	 * 
	 * @return
	 */
	default String CODE_600() {
		return ResultCodeEnum.CODE_600.code;
	}

}
