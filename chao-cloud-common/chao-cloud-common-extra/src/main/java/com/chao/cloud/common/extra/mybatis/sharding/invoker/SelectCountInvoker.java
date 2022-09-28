package com.chao.cloud.common.extra.mybatis.sharding.invoker;

import java.util.List;

import org.aopalliance.intercept.MethodInvocation;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.override.MybatisMapperProxy;
import com.chao.cloud.common.extra.mybatis.config.ShardingTableProperties.ShardingTableRule;
import com.chao.cloud.common.extra.mybatis.constant.MapperEnum;

/**
 * 查询一条<br>
 * 1.{@link BaseMapper#selectCount(com.baomidou.mybatisplus.core.conditions.Wrapper)}<br>
 * 
 * @author 薛超
 * @since 2022年5月18日
 * @version 1.0.0
 */
public class SelectCountInvoker extends AbstractMapperInvoker {

	@Override
	public MapperEnum getMapperMethod() {
		return MapperEnum.count;
	}

	@Override
	public <T> Integer invoke(MybatisMapperProxy<T> mapperProxy, TableInfo tableInfo, ShardingTableRule rule,
			Object[] args, MethodInvocation invocation) throws Throwable {
		// 获取表
		List<String> tableNodes = super.findActualTables(tableInfo, rule, args);
		// 遍历表查询结果并汇总
		return execute(tableNodes, invocation);
	}

}
