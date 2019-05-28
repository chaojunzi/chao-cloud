package com.chao.cloud.common.constants;

/**
 * 返回值类型
 * 
 * @author xuechao
 *
 */
public enum ResultCodeEnum {
	CODE_400("0400"), // 拒绝访问
	CODE_403("0403"), // 权限不足
	CODE_500("0500"), // 业务异常-需要自己处理
	CODE_600("0600"), // 远程业务异常-执行中
	CODE_1("-1"), // 服务异常-如空接口
	CODE_200("0000"); // 成功
	String code;

	ResultCodeEnum(String code) {
		this.code = code;
	}

	public String code() {
		return code;
	}

}