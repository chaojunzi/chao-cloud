package com.chao.cloud.common.extra.ftp.impl;

import java.awt.Color;
import java.awt.Font;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import com.chao.cloud.common.exception.BusinessException;
import com.chao.cloud.common.extra.ftp.IFileOperation;
import com.chao.cloud.common.extra.ftp.annotation.FtpConfig;
import com.chao.cloud.common.extra.ftp.pool.FTPClientPool;

import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.ftp.Ftp;

/**
 * 
 * @功能：单线程访问
 * @author： 薛超
 * @时间： 2019年7月19日
 * @version 1.0.0
 */
public class FileOperationImpl implements IFileOperation {

	private FTPClientPool clientPool;
	private FtpConfig ftpConfig;

	public FileOperationImpl(FTPClientPool clientPool) {
		this.clientPool = clientPool;
		this.ftpConfig = clientPool.getFtpConfig();
	}

	@Override
	public String uploadImg(InputStream is, String fileName) throws Exception {
		// 操作流
		try (InputStream fileStream = is; ByteArrayOutputStream out = new ByteArrayOutputStream();) {
			String mimeType = FileUtil.getMimeType(fileName);
			if (!mimeType.startsWith("image")) {
				throw new BusinessException("无效的图片类型:" + mimeType);
			}
			// 加水印
			ImgUtil.pressText(is, out, //
					ftpConfig.getLogo(), Color.WHITE, // 文字
					new Font(Font.SERIF, Font.BOLD, 20), // 字体
					0, // x坐标修正值。 默认在中间，偏移量相对于中间偏移
					0, // y坐标修正值。 默认在中间，偏移量相对于中间偏移
					ftpConfig.getAlpha()// 透明度：alpha 必须是范围 [0.0, 1.0] 之内（包含边界值）的一个浮点数字
			);
			// 将outputStream转成inputstream
			byte[] array = out.toByteArray();
			ByteArrayInputStream stream = IoUtil.toStream(array);
			// 上传文件
			return this.uploadInputStream(stream, fileName);
		}
	}

	@Override
	public String uploadInputStream(InputStream in, String fileName) throws Exception {
		Ftp ftp = clientPool.borrowObject();// 从线程池获取ftp
		try (InputStream fileStream = in) {
			String name = IFileOperation.genFileName(fileName);
			String path = IFileOperation.genFilePath(ftpConfig.getPath());
			String fullPath = path + name;
			// 判断是否为ftp 上传
			boolean upload = false;
			if (ftpConfig.isLocal()) {// 本地上传
				FileUtil.mkdir(path);
				// copy 图片
				FileUtil.writeFromStream(fileStream, path + name);
				upload = true;
			} else {// ftp 上传
				upload = ftp.upload(path, name, fileStream);
			}
			if (upload) {
				return this.delPathPrefix(fullPath);
			}
		} finally {
			clientPool.returnObject(ftp);// 归还
		}
		throw new BusinessException("文件上传失败:" + fileName);
	}

	@Override
	public void downLoad(String filePath, OutputStream out) throws Exception {
		Ftp ftp = clientPool.borrowObject();// 从线程池获取ftp
		try (OutputStream os = out) {
			final String fileName = FileUtil.getName(filePath);
			final String path = StrUtil.removeSuffix(filePath, fileName);
			ftp.download(path, fileName, os);
		} finally {
			clientPool.returnObject(ftp);// 归还
		}
	}

	@Override
	public boolean delete(String filePath) {
		try {
			Ftp ftp = clientPool.borrowObject();// 从线程池获取ftp
			try {
				return ftp.delFile(filePath);
			} finally {
				clientPool.returnObject(ftp);// 归还
			}
		} catch (Exception e) {
			throw new BusinessException(e.getMessage());
		}

	}

	/**
	 * 去除路径前缀
	 * @param fullPath
	 * @return
	 */
	private String delPathPrefix(String fullPath) {
		String prefix = ftpConfig.getPrefix();
		if (StrUtil.isNotBlank(prefix)) {
			return fullPath.replace(prefix, "");
		}
		return fullPath;
	}
}
