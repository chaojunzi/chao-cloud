package com.chao.cloud.common.extra.mybatis.sharding.invoker;

import java.util.List;

import org.aopalliance.intercept.MethodInvocation;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.override.MybatisMapperProxy;
import com.chao.cloud.common.extra.mybatis.config.ShardingTableProperties.ShardingTableRule;
import com.chao.cloud.common.extra.mybatis.constant.MapperEnum;

/**
 * 修改多条<br>
 * 1.{@link BaseMapper#update(Object, com.baomidou.mybatisplus.core.conditions.Wrapper)}<br>
 * 2.{@link BaseMapper#delete(com.baomidou.mybatisplus.core.conditions.Wrapper)}<br>
 * 3.{@link BaseMapper#deleteByMap(java.util.Map)}<br>
 * 
 * @author 薛超
 * @since 2022年5月19日
 * @version 1.0.0
 */
public class UpdateMultiInvoker extends AbstractMapperInvoker {

	@Override
	public MapperEnum getMapperMethod() {
		return MapperEnum.updateMulti;
	}

	@Override
	public <T> Object invoke(MybatisMapperProxy<T> mapperProxy, TableInfo tableInfo, ShardingTableRule rule,
			Object[] args, MethodInvocation invocation) throws Throwable {
		// 获取表
		List<String> tableNodes = super.findActualTables(tableInfo, rule, args);
		// 遍历表修改多条后汇总
		return execute(tableNodes, invocation);
	}

}
