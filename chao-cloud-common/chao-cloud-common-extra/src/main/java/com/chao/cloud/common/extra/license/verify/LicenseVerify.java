package com.chao.cloud.common.extra.license.verify;

import java.util.prefs.Preferences;

import com.chao.cloud.common.extra.license.CustomKeyStoreParam;
import com.chao.cloud.common.extra.license.CustomLicenseManager;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import de.schlichtherle.license.CipherParam;
import de.schlichtherle.license.DefaultCipherParam;
import de.schlichtherle.license.DefaultLicenseParam;
import de.schlichtherle.license.KeyStoreParam;
import de.schlichtherle.license.LicenseContent;
import de.schlichtherle.license.LicenseManager;
import de.schlichtherle.license.LicenseParam;
import lombok.extern.slf4j.Slf4j;

/**
 * License校验类
 * 
 * @author 薛超
 * @since 2020年12月31日
 * @version 1.0.9
 */
@Slf4j
public class LicenseVerify {

	/**
	 * 安装License证书
	 * 
	 * @param param {@link LicenseVerifyParam}
	 * @return {@link LicenseManager}
	 */
	public LicenseManager install(LicenseVerifyParam param) {
		LicenseParam licenseParam = initLicenseParam(param);
		// 1. 安装证书
		try {
			CustomLicenseManager licenseManager = new CustomLicenseManager(licenseParam);
			licenseManager.uninstall();
			LicenseContent result = licenseManager.install(ResourceUtil.readBytes(param.getLicensePath()));
			log.info("证书安装成功，证书有效期：{} - {}", DateUtil.formatDateTime(result.getNotBefore()),
					DateUtil.formatDateTime(result.getNotAfter()));
			return licenseManager;
		} catch (Exception e) {
			log.error("证书安装失败！");
			throw ExceptionUtil.wrapRuntime(e);
		}
	}

	/**
	 * 校验License证书
	 * 
	 * @param licenseManager {@link LicenseManager}
	 * @return true or false
	 */
	public static boolean verify(LicenseManager licenseManager) {
		// 校验证书
		try {
			LicenseContent licenseContent = licenseManager.verify();
			// log.info("证书校验通过，证书有效期：{} - {}",
			// DateUtil.formatDateTime(licenseContent.getNotBefore()),
			// DateUtil.formatDateTime(licenseContent.getNotAfter()));
			return true;
		} catch (Exception e) {
			log.error("证书校验失败！", e);
			return false;
		}
	}

	private LicenseParam initLicenseParam(LicenseVerifyParam param) {
		Preferences preferences = Preferences.userNodeForPackage(LicenseVerify.class);

		CipherParam cipherParam = new DefaultCipherParam(param.getStorePass());

		KeyStoreParam publicStoreParam = new CustomKeyStoreParam(LicenseVerify.class, param.getPublicKeysStorePath(),
				param.getPublicAlias(), param.getStorePass(), null);

		return new DefaultLicenseParam(param.getSubject(), preferences, publicStoreParam, cipherParam);
	}

}
