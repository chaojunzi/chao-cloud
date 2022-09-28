package com.chao.cloud.common.extra.mybatis.sharding.invoker;

import java.util.List;

import org.aopalliance.intercept.MethodInvocation;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.override.MybatisMapperProxy;
import com.chao.cloud.common.extra.mybatis.config.ShardingTableProperties.ShardingTableRule;
import com.chao.cloud.common.extra.mybatis.constant.MapperEnum;

import cn.hutool.core.util.ObjectUtil;

/**
 * 查询一条<br>
 * 1.{@link BaseMapper#selectById(java.io.Serializable)}<br>
 * 2.{@link BaseMapper#selectOne(com.baomidou.mybatisplus.core.conditions.Wrapper)}
 * 3.{@link BaseMapper#selectBatchIds(java.util.Collection)}
 * 
 * @author 薛超
 * @since 2022年5月18日
 * @version 1.0.0
 */
public class SelectOneInvoker extends AbstractMapperInvoker {

	@Override
	public MapperEnum getMapperMethod() {
		return MapperEnum.selectOne;
	}

	@Override
	public <T> Object invoke(MybatisMapperProxy<T> mapperProxy, TableInfo tableInfo, ShardingTableRule rule,
			Object[] args, MethodInvocation invocation) throws Throwable {
		// 获取表
		List<String> tableNodes = super.findActualTables(tableInfo, rule, args);
		// 遍历表查询获取一条后即刻返回
		Class<?> returnType = invocation.getMethod().getReturnType();
		return executeQuery(tableNodes, r -> ObjectUtil.isNotEmpty(r), invocation, returnType);
	}

}
