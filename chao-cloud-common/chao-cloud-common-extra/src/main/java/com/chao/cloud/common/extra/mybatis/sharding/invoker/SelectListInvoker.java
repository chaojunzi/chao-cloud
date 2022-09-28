package com.chao.cloud.common.extra.mybatis.sharding.invoker;

import java.util.List;
import java.util.Map;

import org.aopalliance.intercept.MethodInvocation;

import com.baomidou.mybatisplus.core.conditions.AbstractWrapper;
import com.baomidou.mybatisplus.core.conditions.ISqlSegment;
import com.baomidou.mybatisplus.core.conditions.segments.MergeSegments;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.override.MybatisMapperProxy;
import com.chao.cloud.common.extra.mybatis.config.ShardingTableProperties.ShardingTableRule;
import com.chao.cloud.common.extra.mybatis.constant.MapperEnum;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;

/**
 * 查询多条<br>
 * 1.{@link BaseMapper#selectList(com.baomidou.mybatisplus.core.conditions.Wrapper)}<br>
 * 2.{@link BaseMapper#selectMaps(com.baomidou.mybatisplus.core.conditions.Wrapper)}
 * 3.{@link BaseMapper#selectObjs(com.baomidou.mybatisplus.core.conditions.Wrapper)}
 * 4.{@link BaseMapper#selectByMap(java.util.Map)}
 * 
 * @author 薛超
 * @since 2022年5月19日
 * @version 1.0.0
 */
public class SelectListInvoker extends AbstractMapperInvoker {

	@Override
	public MapperEnum getMapperMethod() {
		return MapperEnum.list;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public <T> Object invoke(MybatisMapperProxy<T> mapperProxy, TableInfo tableInfo, ShardingTableRule rule,
			Object[] args, MethodInvocation invocation) throws Throwable {
		// 获取表
		List<String> tableNodes = super.findActualTables(tableInfo, rule, args);
		// 遍历表汇总后返回
		Class<?> returnType = invocation.getMethod().getReturnType();
		List<?> list = (List<?>) executeQuery(tableNodes, invocation, returnType);
		// 单表无需排序
		if (CollUtil.size(tableNodes) <= 1 || CollUtil.isEmpty(list)) {
			return list;
		}
		// 排序
		Object entity = CollUtil.getFirst(list);
		if (BeanUtil.isBean(entity.getClass()) || entity instanceof Map) {
			// 获取排序规则
			AbstractWrapper wrapper = getFirstParam(AbstractWrapper.class, args);
			MergeSegments expression = wrapper.getExpression();
			if (expression != null) {
				List<String> orderList = CollUtil.map(expression.getOrderBy(), ISqlSegment::getSqlSegment, true);
				// 排序
				return sort(list, orderList, tableInfo);
			}
		}
		return list;
	}

}
