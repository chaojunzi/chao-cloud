package com.chao.cloud.admin.core.dal.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.chao.cloud.admin.core.dal.entity.StatRequestTime;

@Mapper
public interface XcStatLogMapper {
	
	/**
	 * 执行时间
	 * @return
	 */
	List<StatRequestTime> statRequestTime(String prefix);

}