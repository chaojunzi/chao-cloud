package com.chao.cloud.common.extra.ftp.annotation;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.chao.cloud.common.exception.BusinessException;
import com.chao.cloud.common.extra.ftp.IFileOperation;
import com.chao.cloud.common.extra.ftp.impl.FileOperationImpl;
import com.chao.cloud.common.extra.ftp.pool.FTPClientFactory;
import com.chao.cloud.common.extra.ftp.pool.FTPClientPool;

import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.ftp.Ftp;
import cn.hutool.extra.ftp.FtpMode;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 
 * @功能：ftp 配置
 * @author： 薛超
 * @时间：2019年4月25日
 * @version 2.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Configuration
@ConfigurationProperties(prefix = "chao.cloud.ftp.config")
public class FtpConfig extends GenericObjectPoolConfig<Ftp> implements InitializingBean {

	private boolean local = false;// 默认是ftp
	private String host;
	private Integer port;
	private String user;
	private String password;
	private String prefix;// 返回时去掉的路径
	private String path;// path-根目录
	private String logo;// logo-水印
	private float alpha = 0.1F;// 透明度
	private String domain = StrUtil.EMPTY;// 域名->默认为空
	private String mode = "Passive";// 模式 Passive/Active
	private Ftp ftp;

	@Bean
	public IFileOperation fileOperation(FTPClientPool clientPool) {
		return new FileOperationImpl(clientPool);
	}

	/**
	 * 连接池
	 * @param ftpConfig
	 * @return
	 */
	@Bean
	public FTPClientPool fTPClientPool(FTPClientFactory clientFactory) {
		return new FTPClientPool(clientFactory);
	}

	/**
	 * 工厂
	 * @param ftpConfig
	 * @return
	 */
	@Bean
	public FTPClientFactory fTPClientFactory(FtpConfig ftpConfig) {
		return new FTPClientFactory(ftpConfig);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (local && StrUtil.isBlank(path)) {// 若不设置上传地址->使用classpass下的 public img目录
			this.path = StrUtil.format("{}public/img", ClassUtil.getClassPath());
			this.prefix = StrUtil.removeSuffix(path, "/img");
		}
		if (StrUtil.isBlank(path)) {
			throw new BusinessException("请设置ftp: path");
		}
		if (StrUtil.isBlank(logo)) {
			logo = "chao";
		}
		if (!this.local) {
			Ftp ftp = new Ftp(host, port, user, password);
			// 被动模式
			if (DEFAULT_BLOCK_WHEN_EXHAUSTED) {

			}
			ftp.setMode(this.getMode());
			this.ftp = ftp;
			this.ftp.close();
		}
	}

	private FtpMode getMode() {
		FtpMode[] modes = FtpMode.values();
		for (FtpMode ftpMode : modes) {
			if (ftpMode.name().equalsIgnoreCase(mode)) {
				return ftpMode;
			}
		}
		return null;
	}

}