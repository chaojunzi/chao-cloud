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
import com.chao.cloud.common.extra.ftp.pool.FtpClientProxy;

import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Setter;

/**
 * 文件操作实现（本地操作和ftp）
 * @author 薛超
 * @since 2019年8月1日
 * @version 1.0.5
 */
@Setter
public class FileOperationImpl implements IFileOperation {

	private FtpClientProxy ftpClientProxy;// ftp客户端代理
	private FtpConfig ftpConfig;

	@Override
	public String uploadImg(InputStream in, String fileName) throws Exception {
		// 操作流
		try (InputStream fileStream = in; ByteArrayOutputStream out = new ByteArrayOutputStream();) {
			String mimeType = FileUtil.getMimeType(fileName);
			if (!mimeType.startsWith("image")) {
				throw new BusinessException("无效的图片类型:" + mimeType);
			}
			// 加水印
			ImgUtil.pressText(fileStream, out, //
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
		try (InputStream fileStream = in) {// 从线程池获取ftp
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
				upload = ftpClientProxy.uploadFile(path, name, fileStream);
			}
			if (upload) {
				return this.delPathPrefix(fullPath);
			}
		}
		throw new BusinessException("文件上传失败:" + fileName);
	}

	@Override
	public void downLoad(String filePath, OutputStream out) throws Exception {
		InputStream in = null;
		try (OutputStream os = out;) {
			byte[] bs = ftpClientProxy.retrieveFileStream(filePath);
			in = IoUtil.toStream(bs);
			IoUtil.copy(in, out);
		} finally {
			IoUtil.close(in);
		}
	}

	@Override
	public boolean delete(String filePath) {
		return ftpClientProxy.deleteFile(filePath);

	}

	@Override
	public byte[] downLoadBytes(String filePath) throws Exception {
		return ftpClientProxy.retrieveFileStream(filePath);
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
