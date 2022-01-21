package com.chao.cloud.common.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Comparator;
import java.util.List;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.lang.Filter;
import cn.hutool.core.lang.caller.CallerUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 配置文件工具类
 * 
 * @author 薛超
 * @since 2021年9月2日
 * @version 1.0.0
 */
@Slf4j
public class ConfigUtil {
	/**
	 * 获取文件流
	 * 
	 * @param filePath 文件路径
	 * @return 输入流
	 */
	public static InputStream getStream(String filePath) {
		File file = getFile(filePath);
		Class<?> caller = CallerUtil.getCallerCaller();
		String shortClassName = ClassUtil.getShortClassName(caller.getName());
		if (file == null) {
			log.info("【{}】: {}", shortClassName, filePath);
			return ResourceUtil.getStream(filePath);
		}
		log.info("【{}】: {}", shortClassName, file.getAbsolutePath());
		return FileUtil.getInputStream(file);
	}

	public static byte[] readBytes(String filePath) {
		File file = getFile(filePath);
		Class<?> caller = CallerUtil.getCallerCaller();
		String shortClassName = ClassUtil.getShortClassName(caller.getName());
		if (file == null) {
			log.info("【{}】: {}", shortClassName, filePath);
			return ResourceUtil.readBytes(filePath);
		}
		log.info("【{}】: {}", shortClassName, file.getAbsolutePath());
		return FileUtil.readBytes(file);
	}

	/**
	 * 获取主文件全路径
	 * 
	 * @param filePath 配置文件
	 * @return 主文件全路径
	 * @throws IOException 读取异常
	 */
	public static String getMainFileName(String filePath) {
		return getMainFileName(filePath, null);
	}

	public static String getMainFileName(String filePath, Filter<File> filter) {
		File file = getFile(filePath, filter);
		if (file == null) {
			return null;
		}
		try {
			return FileUtil.normalize(file.getCanonicalPath());
		} catch (Exception e) {
			throw ExceptionUtil.wrapRuntime(e);
		}
	}

	/**
	 * 匹配外部文件
	 * 
	 * @param filePath 文件路径
	 * @return 文件
	 */
	public static File getFile(String filePath) {
		return getFile(filePath, null);
	}

	public static File getFile(String filePath, Filter<File> filter) {
		return getFile(getRootPath(), filePath, filter);
	}

	/**
	 * 文件检索
	 * 
	 * @param rootPath 限制文件搜索范围
	 * @param filePath 文件路径或名称
	 * @param filter   过滤器
	 * @return 匹配到的第一个文件，时间倒叙
	 */
	public static File getFile(String rootPath, String filePath, Filter<File> filter) {
		if (!FileUtil.isAbsolutePath(rootPath)) {
			rootPath = getRootPath();
		}
		// 获取文件名
		String fileName = FileUtil.getName(filePath);
		// 获取当前目录下第一个配置文件
		List<File> configFileList = FileUtil.loopFiles(rootPath, f -> {
			if (StrUtil.equalsAnyIgnoreCase(f.getName(), fileName)) {
				if (filter != null) {
					if (!filter.accept(f)) {
						return false;
					}
				}
				return true;
			}
			return false;
		});
		if (CollUtil.isEmpty(configFileList)) {
			if (FileUtil.isAbsolutePath(filePath) && FileUtil.exist(filePath)) {
				return FileUtil.file(filePath);
			}
			return null;
		}
		// 获取最近的日期
		return configFileList.stream()//
				.sorted(Comparator.comparing(File::lastModified)//
						.reversed()//
						.thenComparingInt(f -> f.getAbsolutePath().length()))//
				.findFirst().orElse(null);
	}

	/**
	 * 获取根目录
	 * 
	 * @return 根目录路径
	 */
	public static String getRootPath() {
		File file = new File("");
		try {
			return FileUtil.normalize(file.getCanonicalPath());
		} catch (Exception e) {
			throw ExceptionUtil.wrapRuntime(e);
		}
	}

	/**
	 * 获取相对路径
	 * 
	 * @param filePath 文件路径
	 * @param prefix   前缀
	 * @return 相对路径
	 */
	public static String getFileRelativePath(String filePath, String prefix) {
		if (StrUtil.hasBlank(filePath, prefix) || !StrUtil.startWith(filePath, prefix)) {
			return filePath;
		}
		return StrUtil.removePrefix(StrUtil.removePrefix(filePath, prefix), StrUtil.SLASH);
	}

	public static String getAbsPath(String path) {
		return getAbsPath(ConfigUtil.getRootPath(), path);
	}

	public static String getAbsPath(String rootPath, String path) {
		if (StrUtil.isBlank(path)) {
			return path;
		}
		if (!FileUtil.isAbsolutePath(path)) {
			// 相对路径获取项目根目录
			path = FileUtil.normalize(StrUtil.format("{}/{}", rootPath, path));
		}
		return path;
	}

}
