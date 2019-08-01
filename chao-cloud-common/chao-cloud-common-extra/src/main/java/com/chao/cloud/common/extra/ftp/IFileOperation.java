package com.chao.cloud.common.extra.ftp;

import java.io.InputStream;
import java.io.OutputStream;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;

/**
 * 文件操作接口
 * @author 薛超
 * @since 2019年8月1日
 * @version 1.0.5
 */
public interface IFileOperation {

	String PATH_DATE_PATTERN = "/yyyy/MM/dd/";

	Snowflake SNOW_FLAKE = IdUtil.createSnowflake(1, 1);

	/**
	 * 上传图片
	 * @param in 图片流
	 * @param fileName 文件路径
	 * @return String 路径
	 * @throws Exception 载时抛出的异常
	 */
	String uploadImg(InputStream in, String fileName) throws Exception;

	/**
	 * 上传文件
	 * @param in 文件流
	 * @param fileName 文件路径
	 * @return String 路径
	 * @throws Exception 载时抛出的异常
	 */
	String uploadInputStream(InputStream in, String fileName) throws Exception;

	/**
	 * 下载
	 * @param filePath 文件路径
	 * @param out 输出流
	 * @throws Exception 载时抛出的异常
	 */
	void downLoad(String filePath, OutputStream out) throws Exception;

	/**
	 * 下载字节
	 * @param filePath 文件路径
	 * @return byte[]
	 * @throws Exception 下载时抛出的异常
	 */
	byte[] downLoadBytes(String filePath) throws Exception;

	/**
	 * 删除文件
	 * @param filePath 文件路径
	 * @return boolean
	 */
	boolean delete(String filePath);

	public static String genFilePath(String rootPath) {
		// 根据时间生成目录结构
		String url = rootPath + DateUtil.format(DateUtil.date(), PATH_DATE_PATTERN);
		return FileUtil.normalize(url);
	}

	public static String genFileName(String fileName) {
		return StrUtil.format("{}.{}", SNOW_FLAKE.nextId(), FileUtil.extName(fileName));
	}

	/**
	 * 生成随机文件全路径
	 * @param rootPath 根目录
	 * @param fileName 文件名（包含路径）
	 * @return String
	 */
	public static String genRandomFullFileName(String rootPath, String fileName) {
		return genFilePath(rootPath) + genFileName(fileName);
	}

}
