package com.chao.cloud.common.extra.ftp.annotation;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.chao.cloud.common.exception.BusinessException;
import com.chao.cloud.common.extra.ftp.IFileOperation;
import com.chao.cloud.common.extra.ftp.impl.FileOperationImpl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.ftp.Ftp;
import cn.hutool.extra.ftp.FtpMode;
import lombok.Data;

/**
 * 
 * @功能：ftp 配置
 * @author： 薛超
 * @时间：2019年4月25日
 * @version 2.0
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "chao.cloud.ftp.config")
public class FtpConfig implements InitializingBean {

	private boolean local = false;// 默认是ftp
	private String host;
	private Integer port;
	private String user;
	private String password;
	private String prefix;// 返回时去掉的路径
	private String path;// path-根目录
	private String logo;// logo-水印
	private Ftp ftp;

	@Bean
	public IFileOperation fileOperation(FtpConfig ftpConfig) {
		return new FileOperationImpl(ftpConfig);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (StrUtil.isBlank(path)) {
			throw new BusinessException("请设置ftp: path");
		}
		if (StrUtil.isBlank(logo)) {
			logo = "chao";
		}
		if (!this.local) {
			Ftp ftp = new Ftp(host, port, user, password);
			// 被动模式
			ftp.setMode(FtpMode.Passive);
			this.ftp = ftp;
		}
	}
}