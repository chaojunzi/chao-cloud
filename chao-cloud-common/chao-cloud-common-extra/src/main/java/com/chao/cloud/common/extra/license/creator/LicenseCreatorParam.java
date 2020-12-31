package com.chao.cloud.common.extra.license.creator;

import java.util.Date;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

import com.chao.cloud.common.extra.license.LicenseCheckModel;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

/**
 * License生成类需要的参数
 * 
 * @author 薛超
 * @since 2020年12月31日
 * @version 1.0.9
 */
@Data
public class LicenseCreatorParam {

	/**
	 * 证书subject-项目编码
	 */
	@NotBlank(message = "项目编码不能为空")
	private String subject;
	/**
	 * 客户名称
	 */
	@NotBlank(message = "客户名称不能为空")
	private String consumerName;
	/**
	 * 证书生效时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@NotNull
	private Date issuedTime;

	/**
	 * 证书失效时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@NotNull
	private Date expiryTime;
	/**
	 * 额外的服务器硬件校验信息
	 */
	@NotNull
	@Valid
	private LicenseCheckModel licenseCheckModel;
	/**
	 * 密钥别称
	 */
	private String privateAlias;

	/**
	 * 密钥密码（需要妥善保管，不能让使用者知道）
	 */
	private String keyPass;

	/**
	 * 访问秘钥库的密码
	 */
	private String storePass;

	/**
	 * 密钥库存储路径
	 */
	private String privateKeysStorePath;

	/**
	 * 用户类型
	 */
	private String consumerType = "user";

	/**
	 * 用户数量
	 */
	private Integer consumerAmount = 1;

	/**
	 * 描述信息
	 */
	private String description = "航天信息版权所有";

}
