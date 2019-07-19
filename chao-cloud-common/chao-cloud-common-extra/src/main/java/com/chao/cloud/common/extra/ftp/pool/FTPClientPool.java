package com.chao.cloud.common.extra.ftp.pool;

import org.apache.commons.pool2.impl.GenericObjectPool;

import com.chao.cloud.common.extra.ftp.annotation.FtpConfig;

import cn.hutool.extra.ftp.Ftp;

/**
 * 
 * @功能：FTP 客户端连接池
 * @author： 薛超
 * @时间： 2019年7月19日
 * @version 1.0.0
 */
public class FTPClientPool {

	/**
	 * ftp客户端连接池
	 */
	private GenericObjectPool<Ftp> pool;

	private FtpConfig ftpConfig;

	/**
	 * 构造函数中 注入一个bean
	 * 
	 * @param clientFactory
	 */
	public FTPClientPool(FTPClientFactory clientFactory) {
		ftpConfig = clientFactory.getFtpConfig();
		pool = new GenericObjectPool<Ftp>(clientFactory, ftpConfig);
	}

	public FtpConfig getFtpConfig() {
		return ftpConfig;
	}

	/**
	 * 借  获取一个连接对象
	 * @return
	 * @throws Exception
	 */
	public Ftp borrowObject() throws Exception {
		return pool.borrowObject();

	}

	/**
	 * 还   归还一个连接对象
	 * @param ftpClient
	 */
	public void returnObject(Ftp ftpClient) {
		if (ftpClient != null) {
			pool.returnObject(ftpClient);
		}
	}
}