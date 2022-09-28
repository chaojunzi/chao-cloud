package com.chao.cloud.common.extra.mybatis.constant;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

/**
 * 关于数据库的一些配置
 */
public class DBContext {

	@Getter
	private static final List<String> dbFileList = new ArrayList<>();

	public static void addDBFile(String dbFile) {
		dbFileList.add(dbFile);
	}
}
