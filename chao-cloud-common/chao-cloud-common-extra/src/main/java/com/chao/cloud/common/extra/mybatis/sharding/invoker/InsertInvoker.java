package com.chao.cloud.common.extra.mybatis.sharding.invoker;

import java.util.Date;

import org.aopalliance.intercept.MethodInvocation;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.override.MybatisMapperProxy;
import com.chao.cloud.common.extra.mybatis.config.ShardingTableProperties.ShardingTableRule;
import com.chao.cloud.common.extra.mybatis.constant.MapperEnum;
import com.chao.cloud.common.extra.mybatis.util.WrapperUtil;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 插入一条 <br>
 * 1.{@link BaseMapper#insert(Object)}
 * 
 * @author 薛超
 * @since 2022年5月18日
 * @version 1.0.0
 */
@Slf4j
public class InsertInvoker extends AbstractMapperInvoker {

	@Override
	public MapperEnum getMapperMethod() {
		return MapperEnum.insert;
	}

	@Override
	public <T> Object invoke(MybatisMapperProxy<T> mapperProxy, TableInfo tableInfo, ShardingTableRule rule,
			Object[] args, MethodInvocation invocation) throws Throwable {
		// 获取参数中的日期范围
		String table = tableInfo.getTableName();
		Class<?> entityType = tableInfo.getEntityType();
		Object entity = getFirstParam(entityType, args);
		String column = rule.getColumn();
		if (entity != null) {
			Date date = this.getPropertyValue(entity, column, tableInfo);
			if (date == null) {
				Date now = DateUtil.dateSecond();
				log.warn("【自动日期分片】: {}.{}={}", table, column, DateUtil.formatDateTime(now));
				String property = WrapperUtil.getProperty(column, tableInfo);
				BeanUtil.setProperty(entity, property, now);
			}
		}
		return invocation.proceed();
	}

}
