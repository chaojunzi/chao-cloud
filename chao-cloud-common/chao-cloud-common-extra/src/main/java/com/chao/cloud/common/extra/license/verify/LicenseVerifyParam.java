package com.chao.cloud.common.extra.license.verify;

import cn.hutool.crypto.asymmetric.SignAlgorithm;
import lombok.Data;

/**
 * License校验类需要的参数
 * 
 * @author 薛超
 * @since 2020年12月31日
 * @version 1.0.9
 */
@Data
public class LicenseVerifyParam {

	/**
	 * 签名算法
	 */
	private SignAlgorithm algorithm = SignAlgorithm.SHA1withRSA;

	/**
	 * 证书subject-项目编码
	 */
	private String subject;

	/**
	 * 公钥别称
	 */
	private String publicAlias = "publicCert";

	/**
	 * 访问公钥库的密码
	 */
	private String storePass = "public_password1234";

	/**
	 * 证书生成路径
	 */
	private String licensePath = "license.lic";

	/**
	 * 密钥库存储路径
	 */
	private String publicKeysStorePath = "publicCerts.keystore";

}
