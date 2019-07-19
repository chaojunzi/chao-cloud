package com.chao.cloud.common.extra.ftp.pool;

import java.io.IOException;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

import com.chao.cloud.common.extra.ftp.annotation.FtpConfig;

import cn.hutool.extra.ftp.Ftp;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @功能：ftpclient 工厂
 * @author： 薛超
 * @时间： 2019年7月19日
 * @version 1.0.0
 */
@Slf4j
@Getter
public class FTPClientFactory extends BasePooledObjectFactory<Ftp> {

	public FTPClientFactory(FtpConfig ftpConfig) {
		this.ftpConfig = ftpConfig;
	}

	private FtpConfig ftpConfig;

	/**
	 * 新建对象
	 */
	@Override
	public Ftp create() throws Exception {
		return ftpConfig.getFtp().init();
	}

	@Override
	public PooledObject<Ftp> wrap(Ftp ftpClient) {
		return new DefaultPooledObject<Ftp>(ftpClient);
	}

	/**
	 * 销毁对象
	 */
	@Override
	public void destroyObject(PooledObject<Ftp> p) throws Exception {
		Ftp ftpClient = p.getObject();
		if (ftpClient != null) {
			ftpClient.close();
		}
		super.destroyObject(p);
	}

	/**
	 * 验证对象
	 */
	@Override
	public boolean validateObject(PooledObject<Ftp> p) {
		Ftp ftpClient = p.getObject();
		boolean connect = false;
		try {
			connect = ftpClient.getClient().sendNoOp();
		} catch (IOException e) {
			log.error("验证ftp连接对象,返回false:{}", e);
		}
		return connect;
	}

}