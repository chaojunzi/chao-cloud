package com.chao.cloud.common.extra.license.creator;

import java.util.prefs.Preferences;

import javax.security.auth.x500.X500Principal;

import com.chao.cloud.common.extra.license.CustomKeyStoreParam;
import com.chao.cloud.common.extra.license.CustomLicenseManager;

import de.schlichtherle.license.CipherParam;
import de.schlichtherle.license.DefaultCipherParam;
import de.schlichtherle.license.DefaultLicenseParam;
import de.schlichtherle.license.KeyStoreParam;
import de.schlichtherle.license.LicenseContent;
import de.schlichtherle.license.LicenseManager;
import de.schlichtherle.license.LicenseParam;

/**
 * License生成类
 * 
 * @author 薛超
 * @since 2020年12月31日
 * @version 1.0.9
 */
public class LicenseCreator {
	private final static X500Principal DEFAULT_HOLDER_AND_ISSUER = new X500Principal(
			"CN=localhost, OU=localhost, O=localhost, L=SH, ST=SH, C=CN");
	private LicenseCreatorParam param;

	public LicenseCreator(LicenseCreatorParam param) {
		this.param = param;
	}

	public byte[] generateLicense() throws Exception {
		LicenseManager licenseManager = new CustomLicenseManager(initLicenseParam());
		LicenseContent licenseContent = initLicenseContent();
		return licenseManager.create(licenseContent);

	}

	private LicenseParam initLicenseParam() {
		Preferences preferences = Preferences.userNodeForPackage(LicenseCreator.class);

		// 设置对证书内容加密的秘钥
		CipherParam cipherParam = new DefaultCipherParam(param.getStorePass());

		KeyStoreParam privateStoreParam = new CustomKeyStoreParam(LicenseCreator.class, param.getPrivateKeysStorePath(),
				param.getPrivateAlias(), param.getStorePass(), param.getKeyPass());

		LicenseParam licenseParam = new DefaultLicenseParam(param.getSubject(), preferences, privateStoreParam,
				cipherParam);

		return licenseParam;
	}

	/**
	 * 设置证书生成正文信息
	 * 
	 * @author zifangsky
	 * @date 2018/4/20 10:57
	 * @since 1.0.0
	 * @return de.schlichtherle.license.LicenseContent
	 */
	private LicenseContent initLicenseContent() {
		LicenseContent licenseContent = new LicenseContent();
		licenseContent.setHolder(DEFAULT_HOLDER_AND_ISSUER);
		licenseContent.setIssuer(DEFAULT_HOLDER_AND_ISSUER);

		licenseContent.setSubject(param.getSubject());
		licenseContent.setIssued(param.getIssuedTime());
		licenseContent.setNotBefore(param.getIssuedTime());
		licenseContent.setNotAfter(param.getExpiryTime());
		licenseContent.setConsumerType(param.getConsumerType());
		licenseContent.setConsumerAmount(param.getConsumerAmount());
		licenseContent.setInfo(param.getDescription());

		// 扩展校验服务器硬件信息
		licenseContent.setExtra(param.getLicenseCheckModel());

		return licenseContent;
	}

}
