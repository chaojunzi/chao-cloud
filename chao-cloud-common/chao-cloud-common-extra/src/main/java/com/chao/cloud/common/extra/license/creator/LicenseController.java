package com.chao.cloud.common.extra.license.creator;

import java.io.InputStream;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.chao.cloud.common.entity.Response;
import com.chao.cloud.common.exception.BusinessException;
import com.chao.cloud.common.extra.license.LicenseCheckModel;
import com.chao.cloud.common.extra.license.config.LicenseCreatorConfig;
import com.chao.cloud.common.util.TinyLinksUtil;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.json.JSONUtil;

/**
 * 用于生成证书文件
 * 
 * @author 薛超
 * @since 2020年12月31日
 * @version 1.0.9
 */
@RestController
@RequestMapping("/license")
@Validated
public class LicenseController implements InitializingBean {

	@Autowired
	private LicenseCreatorConfig config;

	// license请求缓存
	private TimedCache<String, byte[]> licenseCache = CacheUtil.newTimedCache(30L * 60 * 1000);

	@RequestMapping(value = "/subjectCode")
	public Response<Map<String, String>> subjectCode() {
		return Response.ok(config.getSubjectMap());
	}

	@RequestMapping(value = "/generate")
	public Response<String> generate(@Valid LicenseCreatorParam param, HttpServletResponse response) {
		BeanUtil.copyProperties(config, param, CopyOptions.create().ignoreNullValue());
		try {
			// 失效时间必须大于生效时间
			boolean timeError = DateUtil.compare(param.getIssuedTime(), param.getExpiryTime()) >= 0;
			Assert.isFalse(timeError, "失效时间必须晚于生效时间");
			//
			LicenseCreator licenseCreator = new LicenseCreator(param);
			byte[] license = licenseCreator.generateLicense();
			// 转短链存储-30分钟
			String code = TinyLinksUtil.shortUrl(DigestUtil.md5Hex(StrUtil.format("{}~{}:{}@{}@{}", //
					DateUtil.formatDateTime(param.getIssuedTime()), //
					DateUtil.formatDateTime(param.getExpiryTime()), //
					param.getSubject(), //
					param.getConsumerName(), //
					JSONUtil.toJsonStr(param.getLicenseCheckModel())//
			)));
			licenseCache.put(code, license);
			return Response.ok(code);
		} catch (Exception e) {
			if (e.getClass() == RuntimeException.class) {
				e = new BusinessException(StrUtil.format("[证书生成失败! error={}]", e.getMessage()));
			}
			throw ExceptionUtil.wrapRuntime(e);
		}

	}

	@PostMapping(value = "upload")
	public Response<LicenseCheckModel> upload(@NotNull MultipartFile licenseFile) {
		String ciphertext = null;
		try (InputStream in = licenseFile.getInputStream()) {
			// 获取密文
			ciphertext = IoUtil.readUtf8(in);
			Assert.notBlank(ciphertext, "licesne密文解析失败");
			// 解析证书内容
			String json = config.getRsa().decryptStr(ciphertext, KeyType.PrivateKey);
			LicenseCheckModel checkModel = JSONUtil.toBean(json, LicenseCheckModel.class);
			return Response.ok(checkModel);
		} catch (Exception e) {
			throw new BusinessException(StrUtil.format("[证书生成失败! error={},无效的密文：{}]", e.getMessage(), ciphertext));
		}

	}

	@RequestMapping("download/{code}")
	public void download(@PathVariable String code, HttpServletResponse response) {
		// 下载license文件
		String fileName = config.getFileName();
		// 获取文件字节
		byte[] bytes = licenseCache.get(code);
		if (ArrayUtil.isEmpty(bytes)) {
			bytes = StrUtil.bytes("license is expire.");
			fileName = "license-expire.lic";
		}
		// 字符串转字节
		response.setCharacterEncoding(CharsetUtil.UTF_8);
		ServletUtil.write(response, IoUtil.toStream(bytes), "application/octet-stream", fileName);
	}

	@RequestMapping("key-gen")
	public void download(HttpServletResponse response) {
		ServletUtil.write(response, ResourceUtil.getStream("go/key-gen"), "application/octet-stream", "key-gen");
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		licenseCache.schedulePrune(5 * 60 * 1000);// 5分钟清理一次

	}
}
