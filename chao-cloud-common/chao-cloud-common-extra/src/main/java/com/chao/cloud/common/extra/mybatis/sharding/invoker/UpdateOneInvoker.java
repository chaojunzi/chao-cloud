package com.chao.cloud.common.extra.mybatis.sharding.invoker;

import java.util.List;

import org.aopalliance.intercept.MethodInvocation;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.override.MybatisMapperProxy;
import com.chao.cloud.common.extra.mybatis.config.ShardingTableProperties.ShardingTableRule;
import com.chao.cloud.common.extra.mybatis.constant.MapperEnum;

/**
 * 修改或删除一条<br>
 * 1.{@link BaseMapper#deleteById(java.io.Serializable)}<br>
 * 2.{@link BaseMapper#updateById(Object)}<br>
 * 3.{@link BaseMapper#deleteBatchIds(java.util.Collection)}<br>
 * 
 * @author 薛超
 * @since 2022年5月19日
 * @version 1.0.0
 */
public class UpdateOneInvoker extends AbstractMapperInvoker {

	@Override
	public MapperEnum getMapperMethod() {
		return MapperEnum.updateOne;
	}

	@Override
	public <T> Object invoke(MybatisMapperProxy<T> mapperProxy, TableInfo tableInfo, ShardingTableRule rule,
			Object[] args, MethodInvocation invocation) throws Throwable {
		// 获取表
		List<String> tableNodes = super.findActualTables(tableInfo, rule, args);
		// 修改一条就退出循环
		return execute(tableNodes, r -> r > 0, invocation);
	}

}
