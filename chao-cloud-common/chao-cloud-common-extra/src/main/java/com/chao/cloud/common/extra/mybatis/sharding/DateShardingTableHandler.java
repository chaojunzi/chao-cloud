package com.chao.cloud.common.extra.mybatis.sharding;

import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.sql.DataSource;

import org.apache.ibatis.binding.MapperMethod.ParamMap;
import org.apache.ibatis.mapping.SqlCommandType;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.conditions.AbstractWrapper;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.chao.cloud.common.extra.mybatis.common.DateStrategyEnum;
import com.chao.cloud.common.extra.mybatis.config.ShardingTableProperties;
import com.chao.cloud.common.extra.mybatis.config.ShardingTableProperties.ShardingTableRule;
import com.chao.cloud.common.extra.mybatis.plugin.TableNodesComplete;
import com.chao.cloud.common.extra.mybatis.util.MybatisUtil;
import com.chao.cloud.common.extra.mybatis.util.WrapperUtil;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.exceptions.ValidateException;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 日期分片策略-匹配该字段比较合理的日期（中）（注：此处包含or依然生效）<br>
 * 一、不支持内查询：例如<br>
 * 1.create_time = (select * ...) （×）<br>
 * 2.create_time = null（×）<br>
 * 3.不支持自定义sql片段<br>
 * 4.请使用AbstractLambdaWrapper进行构造（√）<br>
 * 5.请尽量使sql简单（√）<br>
 * 二、支持的查询关键字<br>
 * {@link WrapperUtil#supportSqlKeywords}
 * 
 * @author 薛超
 * @since 2022年5月12日
 * @version 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
@SuppressWarnings({ "rawtypes" })
public class DateShardingTableHandler implements ShardingTableHandler {
	// 分片规则
	private final ShardingTableRule rule;

	@Override
	public String dynamicTableName(SqlCommandType type, String sql, String table, Object parameter) {
		DateStrategyEnum dateStrategy = rule.getDateStrategy();
		ShardingEnum se = rule.getType();
		String column = rule.getColumn();
		// 非日期策略不支持分表
		if (StrUtil.isBlank(column) || se != ShardingEnum.DATE || dateStrategy == null) {
			return table;
		}
		/**
		 * 1.循环路由分片
		 */
		String tableName = MapperInvoker.getTableName();
		if (StrUtil.isNotBlank(tableName)) {
			Assert.state(StrUtil.startWith(tableName, table), "表分片匹配失败: {} ∉ {}", tableName, table);
			return getActualTableNode(type, table, tableName);
		}
		/**
		 * 2.根据条件分片
		 */
		List<Date> dateList = parseDate(type, table, parameter);
		CollUtil.removeNull(dateList);
		if (CollUtil.isNotEmpty(dateList)) {
			// 获取最中间的日期
			CollUtil.sort(dateList, Comparator.comparing(Function.identity()));
			Date date = CollUtil.get(dateList, dateList.size() / 2);
			// 生成最终的表节点，判断此日期是否合理（±一个区间）
			String tableNode = this.buildFinalTableNode(type, table, date, dateStrategy);
			return getActualTableNode(type, table, tableNode);
		}
		//
		log.warn("【未识别到分片键-{}】: 使用默认表 {}", column, table);
		return table;
	}

	private String getActualTableNode(SqlCommandType type, String table, String tableNode) {
		// 1.直接进行路由->判断表是否存在
		TableNodesComplete complete = this.getTableNodesComplete();
		// 获取全部表节点
		List<String> tableNodes = complete.getTableNodes(table);
		// 判断表节点是否存在（存在的表节点）
		if (!CollUtil.contains(tableNodes, tableNode)) {
			// 是否生成此表(只有插入操作才会生成，否则无意义)
			if (type != SqlCommandType.INSERT) {
				log.info("【未匹配到日期范围-{}】: 使用默认表 {}", type, table);
				return table;
			}
			// 生成表
			complete.createTableNode(table, CollUtil.toList(tableNode));
		}
		// 判断表是否存在
		log.info("【数据分片-{}】: {}", rule.getColumn(), tableNode);
		return tableNode;
	}

	/**
	 * 由于时间的不确定性，故增加表节点的弹性范围<br>
	 * 
	 * @param type         操作类型
	 * @param table        基本表
	 * @param date         传入的日期
	 * @param dateStrategy 日期策略
	 * @return 最终表节点
	 */
	private String buildFinalTableNode(SqlCommandType type, String table, Date date, DateStrategyEnum dateStrategy) {
		// 表分片配置
		ShardingTableProperties shardingProperties = SpringUtil.getBean(ShardingTableProperties.class);
		String tableNode = dateStrategy.buildTableNode(table, date);
		// 校验
		int low = shardingProperties.getDatetimeLower();
		int up = shardingProperties.getDatetimeUpper();
		// 无限制
		if (low < 0 || up < 0) {
			return tableNode;
		}
		// 判断日期是否超限
		Date now = DateUtil.date();
		// 时间单位
		DateField unit = shardingProperties.getDatetimeUnit();
		Date lowerDate = DateUtil.offset(now, unit, -low);
		Date upperDate = DateUtil.offset(now, unit, up);
		if (DateUtil.compare(date, lowerDate) >= 0 && DateUtil.compare(date, upperDate) <= 0) {
			return tableNode;
		}
		// 范围超限
		throw new ValidateException("【日期范围超限】[{}]: 请传入 {} ~ {} 之间的日期", DateUtil.formatDateTime(date), lowerDate,
				upperDate);
	}

	@SuppressWarnings("unchecked")
	private List<Date> parseDate(SqlCommandType type, String table, Object parameter) {
		String column = rule.getColumn();
		List<Date> dateList = CollUtil.newArrayList();
		TableInfo tableInfo = TableInfoHelper.getTableInfo(table);
		if (tableInfo == null) {
			return dateList;
		}
		if (type == SqlCommandType.INSERT) {// insert默认解析对象
			if (tableInfo.getEntityType() == ClassUtil.getClass(parameter)) {
				String property = WrapperUtil.getProperty(column, tableInfo);
				Object v = BeanUtil.getFieldValue(parameter, property);
				dateList.add((Date) v);
				return dateList;
			}
		}
		// 参数解析
		if (parameter instanceof ParamMap) {
			ParamMap paramMap = (ParamMap) parameter;
			Collection values = paramMap.values();
			for (Object val : values) {
				if (val instanceof Map) {
					Map<String, Object> dataMap = (Map<String, Object>) val;
					Date date = MapUtil.getDate(dataMap, column);
					if (date == null) {
						String property = WrapperUtil.getProperty(column, tableInfo);
						date = MapUtil.getDate(dataMap, property);
					}
					if (date != null) {
						dateList.add(date);
						break;
					}
				}
				if (val instanceof AbstractWrapper) {
					DateRangeModel model = WrapperUtil.parseDateRange((AbstractWrapper) val, column, paramMap);
					if (model != null) {
						dateList.addAll(model.getDateList());
						Date lowerDate = model.getLowerDate();
						if (lowerDate != null) {
							dateList.add(lowerDate);
						}
						Date upperDate = model.getUpperDate();
						if (upperDate != null) {
							dateList.add(upperDate);
						}
					}
					break;
				}
			}
		}
		return dateList;
	}

	private TableNodesComplete getTableNodesComplete() {
		// 获取当前数据源-并获取数据源类型
		DataSource ds = SpringUtil.getBean(DataSource.class);
		DbType dbType = MybatisUtil.getDbType(ds);
		Map<String, TableNodesComplete> beans = SpringUtil.getBeansOfType(TableNodesComplete.class);
		Assert.notNull(beans, "请注入一个生成表的实现类：{}", TableNodesComplete.class.getSimpleName());
		TableNodesComplete complete = CollUtil.findOne(beans.values(), b -> b.getDbType() == dbType);
		//
		Assert.notNull(complete, "未找到该数据库表生成插件：{}", dbType);
		return complete;

	}
}