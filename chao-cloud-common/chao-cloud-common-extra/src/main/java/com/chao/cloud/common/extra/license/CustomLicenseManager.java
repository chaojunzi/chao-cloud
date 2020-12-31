package com.chao.cloud.common.extra.license;

import java.beans.XMLDecoder;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.system.OsInfo;
import cn.hutool.system.SystemUtil;
import de.schlichtherle.license.LicenseContent;
import de.schlichtherle.license.LicenseContentException;
import de.schlichtherle.license.LicenseManager;
import de.schlichtherle.license.LicenseNotary;
import de.schlichtherle.license.LicenseParam;
import de.schlichtherle.license.NoLicenseInstalledException;
import de.schlichtherle.xml.GenericCertificate;
import lombok.extern.slf4j.Slf4j;

/**
 * 自定义LicenseManager，用于增加额外的服务器硬件信息校验
 * 
 * @author 薛超
 * @since 2020年12月31日
 * @version 1.0.9
 */
@Slf4j
public class CustomLicenseManager extends LicenseManager {

	public CustomLicenseManager(LicenseParam param) {
		super(param);
	}

	@Override
	protected synchronized byte[] create(LicenseContent content, LicenseNotary notary) throws Exception {
		initialize(content);
		this.validateCreate(content);
		final GenericCertificate certificate = notary.sign(content);
		return getPrivacyGuard().cert2key(certificate);
	}

	/**
	 * 安装许可证
	 * 
	 * @param license license字节
	 * @return {@link LicenseContent}
	 * @throws Exception 异常
	 */
	public synchronized LicenseContent install(byte[] license) throws Exception {
		LicenseNotary notary = getLicenseNotary();
		return install(license, notary);
	}

	@Override
	protected synchronized LicenseContent install(final byte[] key, final LicenseNotary notary) throws Exception {
		final GenericCertificate certificate = getPrivacyGuard().key2cert(key);

		notary.verify(certificate);
		final LicenseContent content = (LicenseContent) this.load(certificate.getEncoded());
		setLicenseKey(key);
		setCertificate(certificate);
		this.validate(content);
		return content;
	}

	@Override
	protected synchronized LicenseContent verify(final LicenseNotary notary) throws Exception {
		GenericCertificate certificate = getCertificate();

		// Load license key from preferences,
		final byte[] key = getLicenseKey();
		if (null == key) {
			throw new NoLicenseInstalledException(getLicenseParam().getSubject());
		}

		certificate = getPrivacyGuard().key2cert(key);
		notary.verify(certificate);
		final LicenseContent content = (LicenseContent) this.load(certificate.getEncoded());
		this.validate(content);
		setCertificate(certificate);

		return content;
	}

	protected synchronized void validateCreate(final LicenseContent content) throws LicenseContentException {

		final Date now = new Date();
		final Date notBefore = content.getNotBefore();
		final Date notAfter = content.getNotAfter();
		if (null != notAfter && now.after(notAfter)) {
			throw new LicenseContentException("证书失效时间不能早于当前时间");
		}
		if (null != notBefore && null != notAfter && notAfter.before(notBefore)) {
			throw new LicenseContentException("证书生效时间不能晚于证书失效时间");
		}
		final String consumerType = content.getConsumerType();
		if (null == consumerType) {
			throw new LicenseContentException("用户类型不能为空");
		}
	}

	@Override
	protected synchronized void validate(final LicenseContent content) throws LicenseContentException {
		// 1. 首先调用父类的validate方法
		super.validate(content);

		// 2. 然后校验自定义的License参数
		// License中可被允许的参数信息
		LicenseCheckModel licenseModel = (LicenseCheckModel) content.getExtra();
		// 当前服务器真实的参数信息
		LicenseCheckModel serverModel = getServerInfos();
		if (licenseModel != null && serverModel != null) {
			// 校验IP地址
			if (!checkIpAddress(licenseModel.getIpAddress(), serverModel.getIpAddress())) {
				log.error("Invalid IP:{}", CollUtil.join(serverModel.getIpAddress(), StrUtil.SPACE));
				throw new LicenseContentException("当前服务器的IP没在授权范围内");
			}

			// 校验Mac地址
			if (!checkIpAddress(licenseModel.getMacAddress(), serverModel.getMacAddress())) {
				log.error("Invalid MAC:{}", CollUtil.join(serverModel.getMacAddress(), StrUtil.SPACE));
				throw new LicenseContentException("当前服务器的Mac地址没在授权范围内");
			}

			// 校验主板序列号
			if (!checkSerial(licenseModel.getMainBoardSerial(), serverModel.getMainBoardSerial())) {
				// throw new LicenseContentException("当前服务器的主板序列号没在授权范围内");
				log.warn("当前服务器的主板序列号没在授权范围内");
			}

			// 校验CPU序列号
			if (!checkSerial(licenseModel.getCpuSerial(), serverModel.getCpuSerial())) {
				// throw new LicenseContentException("当前服务器的CPU序列号没在授权范围内");
				log.warn("当前服务器的CPU序列号没在授权范围内");
			}
		} else {
			throw new LicenseContentException("不能获取服务器硬件信息");
		}
	}

	private Object load(String encoded) throws IOException {
		try (InputStream in = //
				new BufferedInputStream(new ByteArrayInputStream(encoded.getBytes(CharsetUtil.UTF_8)));
				XMLDecoder decoder = new XMLDecoder(new BufferedInputStream(in, IoUtil.DEFAULT_BUFFER_SIZE), null,
						null)) {
			return decoder.readObject();
		} catch (UnsupportedEncodingException e) {
			log.error("XMLDecoder解析XML失败", e);
		}
		return null;
	}

	private LicenseCheckModel getServerInfos() {
		OsInfo os = SystemUtil.getOsInfo();
		ServerInfo info = os.isWindows() ? new WindowsServerInfo() : new LinuxServerInfo();
		LicenseCheckModel result = new LicenseCheckModel();
		try {
			Set<InetAddress> ipAddress = ServerInfo.getIpAddress();
			result.setIpAddress(CollUtil.newArrayList(NetUtil.toIpList(ipAddress)));
			List<String> macAddress = ipAddress.stream().map(addr -> NetUtil.getMacAddress(addr, StrUtil.COLON))
					.collect(Collectors.toList());
			result.setMacAddress(macAddress);
			result.setCpuSerial(info.getCPUSerial());
			result.setMainBoardSerial(info.getMainBoardSerial());
		} catch (Exception e) {
			log.error("获取服务器硬件信息失败", e);
		}
		return result;
	}

	/**
	 * 校验当前服务器的IP/Mac地址是否在可被允许的IP范围内<br/>
	 * 如果存在IP在可被允许的IP/Mac地址范围内，则返回true
	 * 
	 * @author zifangsky
	 * @date 2018/4/24 11:44
	 * @since 1.0.0
	 * @return boolean
	 */
	private boolean checkIpAddress(List<String> expectedList, List<String> serverList) {
		// 判断是否存在交集
		return CollUtil.containsAny(expectedList, serverList);
	}

	/**
	 * 校验当前服务器硬件（主板、CPU等）序列号是否在可允许范围内
	 * 
	 * @author zifangsky
	 * @date 2018/4/24 14:38
	 * @since 1.0.0
	 * @return boolean
	 */
	private boolean checkSerial(String expectedSerial, String serverSerial) {
		return StrUtil.isBlank(expectedSerial) || StrUtil.equals(expectedSerial, serverSerial);
	}

}
