package com.chao.cloud.admin.sys.dal.xcmapper;

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

	@Select("select table_name tableName, engine, table_comment tableComment, create_time createTime from information_schema.tables"
			+ " where table_schema = (select database()) ")
	List<Map<String, Object>> list();

}
