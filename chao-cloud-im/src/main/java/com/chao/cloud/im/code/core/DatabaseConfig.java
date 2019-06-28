package com.chao.cloud.im.code.core;

import java.util.List;
import java.util.stream.Collectors;

import cn.hutool.core.io.FileUtil;

/**
 * 数据库->转H2
 * @功能： 
 * @author： 薛超
 * @时间： 2019年6月10日
 * @version 1.0.0
 */
public class DatabaseConfig {

	public static void main(String[] args) {
		mysqlToH2("D:/data/carpool/schema.sql");
		mysqlToH2("D:/data/carpool/data.sql");
	}

	private static void mysqlToH2(String path) {
		List<String> lines = FileUtil.readUtf8Lines(path);
		// 操作字符串
		List<String> list = lines.stream().map(l -> {
			if (l.contains("ENGINE=")) {
				return ");";
			}
			return l;
		}).collect(Collectors.toList());
		// 写
		FileUtil.writeUtf8Lines(list, path);
	}

}
