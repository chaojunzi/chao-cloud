package com.chao.cloud.common.extra.license;

import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.NotEmpty;

import lombok.Data;

/**
 * 自定义需要校验的License参数
 * 
 * @author 薛超
 * @since 2020年12月31日
 * @version 1.0.9
 */
@Data
public class LicenseCheckModel implements Serializable {

	private static final long serialVersionUID = 1L;
	/**
	 * 可被允许的IP地址
	 */
	@NotEmpty(message = "ip地址不能为空")
	private List<String> ipAddress;

	/**
	 * 可被允许的MAC地址
	 */
	@NotEmpty(message = "MAC地址不能为空")
	private List<String> macAddress;

	/**
	 * 可被允许的CPU序列号
	 */
	private String cpuSerial;

	/**
	 * 可被允许的主板序列号
	 */
	private String mainBoardSerial;

}
