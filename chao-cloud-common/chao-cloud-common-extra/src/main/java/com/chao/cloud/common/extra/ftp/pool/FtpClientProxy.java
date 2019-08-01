package com.chao.cloud.common.extra.ftp.pool;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTPClient;

import com.chao.cloud.common.exception.BusinessException;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * ftp上传下载工具类（代理）
 */
@Slf4j
public class FtpClientProxy {

	/**
	 * ftp连接池
	 */
	private FtpClientPool ftpClientPool;

	public FtpClientProxy(FtpClientPool ftpClientPool) {
		this.ftpClientPool = ftpClientPool;
	}

	/**
	 * 向FTP服务器上传文件
	 * @param filePath  FTP服务器文件存放路径。例如分日期存放：/2015/01/01。文件的路径为basePath+filePath
	 * @param filename  上传到FTP服务器上的文件名
	 * @param in  输入流
	 * @return 成功返回true，否则返回false isClose 是否关闭流 （慎用）
	 * @throws IOException 上传时抛出的异常
	 */
	public boolean uploadFile(String filePath, String filename, InputStream in) throws IOException {
		boolean result = false;
		FTPClient ftp = null;
		try {
			ftp = ftpClientPool.borrowObject();
		} catch (Exception e) {
			log.error("{}", e);
			// 获取客户端失败
			return result;
		}
		try {
			// 切换到上传目录
			if (!ftp.changeWorkingDirectory(filePath)) {
				// 如果目录不存在创建目录
				String[] dirs = filePath.split("/");
				StrBuilder tempPath = StrUtil.strBuilder();
				for (String dir : dirs) {
					if (StrUtil.isBlank(dir)) {
						continue;
					}
					tempPath.append(StrUtil.format("/{}", dir));
					if (!ftp.changeWorkingDirectory(tempPath.toString())) {
						if (!ftp.makeDirectory(tempPath.toString())) {
							return result;
						} else {
							ftp.changeWorkingDirectory(tempPath.toString());
						}
					}
				}
			}
			// 上传文件
			if (!ftp.storeFile(filename, in)) {
				return result;
			}
			result = true;
		} catch (IOException e) {
			log.error("{}", e);
		} finally {
			ftpClientPool.returnObject(ftp);
			IoUtil.close(in);
		}
		return result;
	}

	/**
	 * 下载 remote文件流
	 * @param remote  远程文件
	 * @return 字节数据
	 * @throws Exception 下载时抛出的异常
	 */
	public byte[] retrieveFileStream(String remote) throws Exception {
		FTPClient client = null;
		InputStream in = null;
		boolean nullIO = false;
		try {
			client = ftpClientPool.borrowObject();
			// 判断文件是否存在
			in = client.retrieveFileStream(remote);
			if (in == null) {
				nullIO = true;
				throw new BusinessException("无效的文件名：" + remote);
			}
			byte[] bytes = IOUtils.toByteArray(in);
			return bytes;
		} finally {
			IoUtil.close(in);
			if (!nullIO && !client.completePendingCommand()) {
				client.logout();
				client.disconnect();
				ftpClientPool.getPool().invalidateObject(client);
			}
			ftpClientPool.returnObject(client);

		}
	}

	/**
	 * 删除目录，单个不可递归
	 * @param pathname 文件路径
	 * @return boolean 
	 * @throws IOException 删除时抛出的异常
	 */
	public boolean removeDirectory(String pathname) throws Exception {
		FTPClient client = null;
		try {
			client = ftpClientPool.borrowObject();
			return client.removeDirectory(pathname);
		} finally {
			ftpClientPool.returnObject(client);
		}
	}

	/**
	 * 删除文件 ，不可递归
	 * 
	 * @param pathname 文件路径
	 * @return boolean
	 */
	public boolean deleteFile(String pathname) {
		FTPClient client = null;
		try {
			client = ftpClientPool.borrowObject();
			return client.deleteFile(pathname);
		} catch (Exception e) {
			log.error("{}", e);
			return false;
		} finally {
			ftpClientPool.returnObject(client);
		}
	}

}
