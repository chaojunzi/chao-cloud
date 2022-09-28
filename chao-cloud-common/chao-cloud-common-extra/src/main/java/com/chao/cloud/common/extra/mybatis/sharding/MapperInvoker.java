package com.chao.cloud.common.extra.mybatis.sharding;

import org.aopalliance.intercept.MethodInvocation;

import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.override.MybatisMapperProxy;
import com.chao.cloud.common.extra.mybatis.config.ShardingTableProperties.ShardingTableRule;
import com.chao.cloud.common.extra.mybatis.constant.MapperEnum;

/**
 * mapper方法代理
 * 
 * @author 薛超
 * @since 2022年5月17日
 * @version 1.0.0
 */
public interface MapperInvoker {

	/**
	 * 当前线程分片表名
	 */
	ThreadLocal<String> TABLE_HOLDER = new ThreadLocal<>();

	static String getTableName() {
		return TABLE_HOLDER.get();
	}

	static void setTableName(String tableName) {
		TABLE_HOLDER.set(tableName);
	}

	static void remove() {
		TABLE_HOLDER.remove();
	}

	/**
	 * mapper属性
	 */
	String mapperField = "mapperInterface";

	/**
	 * 获取方法类型
	 */
	MapperEnum getMapperMethod();

	/**
	 * 执行方法<br>
	 * 1.判断参数中是否包含日期<br>
	 * 2.获取对象及表名
	 * 
	 * @param tableInfo  表信息
	 * @param rule       分片规则
	 * @param args       参数列表
	 * @param invocation 代理对象
	 * @return 执行结果
	 */
	<T> Object invoke(MybatisMapperProxy<T> mapperProxy, TableInfo tableInfo, ShardingTableRule rule, Object[] args,
			MethodInvocation invocation) throws Throwable;

}
