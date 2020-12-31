package com.chao.cloud.common.extra.license.config;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.chao.cloud.common.extra.license.CustomKeyStoreParam;
import com.chao.cloud.common.extra.license.creator.LicenseCreator;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.lang.Console;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.RSA;
import cn.hutool.crypto.asymmetric.SignAlgorithm;
import de.schlichtherle.license.KeyStoreParam;
import de.schlichtherle.license.LicenseNotary;
import lombok.Data;

/**
 * 生成license
 * 
 * @author 薛超
 * @since 2020年12月31日
 * @version 1.0.9
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "license.creator")
public class LicenseCreatorConfig implements InitializingBean, LicenseInit {

	/**
	 * 项目编码
	 */
	private Map<String, String> subjectMap = new HashMap<>();

	/**
	 * 许可证文件名字
	 */
	private String fileName = "license.lic";
	/**
	 * 密钥别称
	 */
	private String privateAlias = "privateKey";
	private transient String publicAlias = "publicCert";
	/**
	 * 密钥密码（需要妥善保管，不能让使用者知道）
	 */
	private String keyPass = "";

	/**
	 * 访问秘钥库的密码
	 */
	private String storePass = "";

	/**
	 * 私钥库存储路径
	 */
	private String privateKeysStorePath = "privateKeys.keystore";
	/**
	 * 公钥库存储路径
	 */
	private String publicKeysStorePath = "publicCerts.keystore";
	/**
	 * 签名算法
	 */
	private SignAlgorithm algorithm = SignAlgorithm.SHA1withRSA;

	private RSA rsa;

	@Override
	public void afterPropertiesSet() throws Exception {
		// 修改签名算法
		setLicenseNotaryAlgorithm(algorithm);
		// 获取私钥
		KeyStoreParam priStoreParam = new CustomKeyStoreParam(LicenseCreator.class, privateKeysStorePath, privateAlias,
				storePass, keyPass);
		LicenseNotary priNotary = new LicenseNotary(priStoreParam);
		//
		PrivateKey priKey = ReflectUtil.invoke(priNotary, "getPrivateKey");
		rsa = SecureUtil.rsa(Base64.encode(priKey.getEncoded()), null);
	}

	public static void main(String[] args) throws Exception {
		LicenseCreatorConfig config = new LicenseCreatorConfig();
		KeyStoreParam priStoreParam = new CustomKeyStoreParam(LicenseCreator.class, config.getPrivateKeysStorePath(),
				config.getPrivateAlias(), config.getStorePass(), config.getKeyPass());
		//
		LicenseNotary priNotary = new LicenseNotary(priStoreParam);
		//
		PrivateKey priKey = ReflectUtil.invoke(priNotary, "getPrivateKey");
		Console.log("私钥:\n{}", Base64.encode(priKey.getEncoded()));

		KeyStoreParam pubStoreParam = new CustomKeyStoreParam(LicenseCreator.class, config.getPublicKeysStorePath(),
				config.getPublicAlias(), config.getStorePass(), null);
		LicenseNotary pubNotary = new LicenseNotary(pubStoreParam);
		PublicKey pubKey = ReflectUtil.invoke(pubNotary, "getPublicKey");
		Console.log("公钥:\n{}", Base64.encode(pubKey.getEncoded()));
	}

}
