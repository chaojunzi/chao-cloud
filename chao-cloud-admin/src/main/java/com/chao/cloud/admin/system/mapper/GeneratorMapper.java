package com.chao.cloud.admin.system.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 
 * @功能：查询表信息
 * @author： 薛超
 * @时间： 2019年5月27日
 * @version 1.0.0
 */
@Mapper
public interface GeneratorMapper {
	/**
	 * 只查询fc 开头的表
	 */
	String filter = " AND table_name REGEXP '^'";

	@Select("select table_name tableName, engine, table_comment tableComment, create_time createTime from information_schema.tables"
			+ " where table_schema = (select database()) " + filter)
	List<Map<String, Object>> list();

	@Select("select count(*) from information_schema.tables where table_schema = (select database())" + filter)
	int count(Map<String, Object> map);

	@Select("select table_name tableName, engine, table_comment tableComment, create_time createTime from information_schema.tables \r\n"
			+ "	where table_schema = (select database()) and table_name = #{tableName}")
	Map<String, String> get(String tableName);

	@Select("select column_name columnName, data_type dataType, column_comment columnComment, column_key columnKey, extra from information_schema.columns\r\n"
			+ " where table_name = #{tableName} and table_schema = (select database()) order by ordinal_position")
	List<Map<String, String>> listColumns(String tableName);
}
