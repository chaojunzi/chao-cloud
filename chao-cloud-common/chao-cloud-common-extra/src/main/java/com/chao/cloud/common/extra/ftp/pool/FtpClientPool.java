package com.chao.cloud.common.extra.ftp.pool;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.pool2.impl.GenericObjectPool;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FtpClientPool {
	private GenericObjectPool<FTPClient> genericObjectPool;

	public FtpClientPool(GenericObjectPool<FTPClient> genericObjectPool) {
		this.genericObjectPool = genericObjectPool;
	}

	public FTPClient borrowObject() throws Exception {
		return genericObjectPool.borrowObject();
	}

	public GenericObjectPool<FTPClient> getPool() {
		return genericObjectPool;
	}

	public void returnObject(FTPClient ftpClient) {
		genericObjectPool.returnObject(ftpClient);
		log.info("ftp归还后");
		log.info("活动" + genericObjectPool.getNumActive());
		log.info("等待" + genericObjectPool.getNumWaiters());
		log.info("----------");
	}
}