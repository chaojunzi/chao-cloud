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
 * @功能：
 * @author： 薛超
 * @时间： 2019年5月27日
 * @version 1.0.0
 */
public interface IFileOperation {

	String PATH_DATE_PATTERN = "/yyyy/MM/dd/";

	Snowflake SNOW_FLAKE = IdUtil.createSnowflake(1, 1);

	/**
	 * 上传图片
	 * 
	 * @param t
	 * @return 文件名字
	 */
	String uploadImg(InputStream is, String fileName) throws Exception;

	/**
	 * 
	 * @param is
	 *            文件流
	 * @param fileName
	 *            文件名称
	 * @return
	 * @throws Exception
	 */
	String uploadInputStream(InputStream is, String fileName) throws Exception;

	/**
	 * 下载
	 */
	void downLoad(String filePath, OutputStream out) throws Exception;

	/**
	 * 下载字节
	 * @param filePath
	 */
	byte[] downLoadBytes(String filePath) throws Exception;

	/**
	 * 删除文件
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
	 * @param fileName
	 * @return
	 */
	public static String genRandomFullFileName(String rootPath, String fileName) {
		return genFilePath(rootPath) + genFileName(fileName);
	}

}
