package com.chao.cloud.common.extra.license;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import cn.hutool.core.io.resource.ResourceUtil;
import de.schlichtherle.license.AbstractKeyStoreParam;
import lombok.Getter;

/**
 * 自定义KeyStoreParam，用于将公私钥存储文件存放到其他磁盘位置而不是项目中
 * 
 * @author 薛超
 * @since 2020年12月31日
 * @version 1.0.9
 */
@Getter
public class CustomKeyStoreParam extends AbstractKeyStoreParam {

	/**
	 * 公钥/私钥在磁盘上的存储路径
	 */
	private String storePath;
	private String alias;
	private String storePwd;
	private String keyPwd;

	public CustomKeyStoreParam(Class<?> clazz, String resource, String alias, String storePwd, String keyPwd) {
		super(clazz, resource);
		this.storePath = resource;
		this.alias = alias;
		this.storePwd = storePwd;
		this.keyPwd = keyPwd;
	}

	@Override
	public InputStream getStream() throws IOException {
		final InputStream in = ResourceUtil.getStream(storePath);
		if (null == in) {
			throw new FileNotFoundException(storePath);
		}
		return in;
	}
}