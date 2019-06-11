package com.chao.cloud.admin.sys.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.chao.cloud.admin.sys.constant.IDatabaseInfo;

/**
 * 
 * @功能：代码生成
 * @author： 薛超
 * @时间：2019年5月20日
 * @version 2.0
 */
@Service
public interface GeneratorService {
	/**
	 * 查询表信息
	 * @param iDbQuery
	 * @param connection
	 * @return
	 */
	List<IDatabaseInfo> list(String tableName);

}
