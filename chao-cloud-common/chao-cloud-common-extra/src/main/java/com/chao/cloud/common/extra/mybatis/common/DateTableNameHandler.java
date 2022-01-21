package com.chao.cloud.common.extra.mybatis.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.apache.ibatis.binding.MapperMethod.ParamMap;
import org.apache.ibatis.mapping.SqlCommandType;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.conditions.AbstractWrapper;
import com.baomidou.mybatisplus.core.conditions.ISqlSegment;
import com.baomidou.mybatisplus.core.conditions.segments.MergeSegments;
import com.baomidou.mybatisplus.core.conditions.segments.NormalSegmentList;
import com.baomidou.mybatisplus.core.enums.SqlKeyword;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.chao.cloud.common.extra.mybatis.dynamic.DynamicTableRuleProperties.ShardingTableRule;
import com.chao.cloud.common.extra.mybatis.plugin.TableNodesComplete;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.comparator.CompareUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.exceptions.ValidateException;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReUtil;
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
 * {@link DateTableNameHandler#supportSqlKeywords}
 * 
 * @author 薛超
 * @since 2021年12月21日
 * @version 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
@SuppressWarnings({ "rawtypes" })
public class DateTableNameHandler implements ShardTableHandler {
	// 分片规则
	private final ShardingTableRule rule;
	//
	private static final String paramRegex = "#\\{([\\w\\.]+)\\}";
	/**
	 * = ?<br>
	 * in (?,...) <br>
	 * between ? and ?<br>
	 * >= ?<br>
	 * > ?<br>
	 * <= ? <br>
	 * < ?
	 */
	private static final List<SqlKeyword> supportSqlKeywords = CollUtil.toList(//
			SqlKeyword.EQ, //
			SqlKeyword.IN, //
			SqlKeyword.BETWEEN, //
			SqlKeyword.GE, //
			SqlKeyword.GT, //
			SqlKeyword.LE, //
			SqlKeyword.LT);

	@Override
	public String dynamicTableName(SqlCommandType type, String sql, String table, Object parameter) {
		DateStrategyEnum dateStrategy = rule.getDateStrategy();
		ShardEnum se = rule.getType();
		// 非日期策略不支持分表
		if (se != ShardEnum.DATE || dateStrategy == null) {
			return table;
		}
		// 根据列进行分片处理
		String column = rule.getColumn();
		List<Date> dateList = parseDate(type, table, parameter);
		CollUtil.removeNull(dateList);
		if (CollUtil.isNotEmpty(dateList)) {
			// 获取最中间的日期
			CollUtil.sort(dateList, Comparator.comparing(Function.identity()));
			Date date = CollUtil.get(dateList, dateList.size() / 2);
			// 生成最终的表节点，判断此日期是否合理（±一个区间）
			String tableNode = this.buildFinalTableNode(table, date, dateStrategy);
			// 表节点生成器
			TableNodesComplete complete = this.getTableNodesComplete();
			// 获取全部表节点
			List<String> tableNodes = complete.getTableNodes(table);
			// 判断表节点是否存在（存在的表节点）
			if (!CollUtil.contains(tableNodes, tableNode)) {
				// 是否生成此表(只有插入操作才会生成，否则无意义)
				if (type == SqlCommandType.INSERT) {
					// 生成表
					complete.createTableNode(table, CollUtil.toList(tableNode));
				} else {// 使用默认表
					tableNode = table;
				}
			}
			// 判断表是否存在
			log.info("【数据分片-{}】: {}", column, tableNode);
			return tableNode;
		}
		//
		log.warn("【未识别到分片键-{}】: 使用默认表 {}", column, table);
		return table;
	}

	/**
	 * 由于时间的不确定性，故增加表节点的弹性范围±一个日期区间<br>
	 * 例如：month-1 -> 则当前时间±1个月
	 * 
	 * @param table        基本表
	 * @param date         传入的日期
	 * @param dateStrategy 日期策略
	 * @return 最终表节点
	 */
	private String buildFinalTableNode(String table, Date date, DateStrategyEnum dateStrategy) {
		// 当前表节点
		Date current = DateUtil.date();
		String currentNode = dateStrategy.buildTableNode(table, current);
		// 目标表节点
		String tableNode = dateStrategy.buildTableNode(table, date);
		//
		int range = CompareUtil.compare(tableNode, currentNode);
		if (range == 0) {
			return tableNode;
		}
		// 判断±月份
		boolean gt = range > 0;
		int offset = dateStrategy.getMonth() * (gt ? 1 : -1);
		// 弹性后的日期
		String finalTableNode = dateStrategy.buildTableNode(table, DateUtil.offsetMonth(current, offset));
		// 判断节点是否超限
		int limit = CompareUtil.compare(tableNode, finalTableNode);
		boolean more = gt && limit > 0;
		boolean less = !gt && limit < 0;
		if (more || less) {
			DateTime leftDate = DateUtil.beginOfMonth(DateUtil.offsetMonth(current, -dateStrategy.getMonth()));
			DateTime rightDate = DateUtil.endOfMonth(DateUtil.offsetMonth(current, dateStrategy.getMonth()));
			throw new ValidateException("【日期范围超限】[{}]: 请传入 {} ~ {} 之间的日期", DateUtil.formatDateTime(date), leftDate,
					rightDate);
		}
		return finalTableNode;
	}

	private TableNodesComplete getTableNodesComplete() {
		Map<String, TableNodesComplete> beans = SpringUtil.getBeansOfType(TableNodesComplete.class);
		Assert.notNull(beans, "请注入一个生成表的实现类：{}", TableNodesComplete.class.getSimpleName());
		DbType dbType = rule.getDbType();
		TableNodesComplete complete = CollUtil.findOne(beans.values(), b -> b.getDbType() == dbType);
		Assert.notNull(complete, "未找到该数据库表生成插件：{}", dbType);
		return complete;
	}

	private List<Date> parseDate(SqlCommandType type, String table, Object parameter) {
		String column = rule.getColumn();
		List<Date> dateList = CollUtil.newArrayList();
		if (type == SqlCommandType.INSERT) {// insert默认解析对象
			TableInfo tableInfo = TableInfoHelper.getTableInfo(table);
			if (tableInfo == null) {
				return dateList;
			}
			if (tableInfo.getEntityType() == ClassUtil.getClass(parameter)) {
				List<TableFieldInfo> fieldList = tableInfo.getFieldList();
				TableFieldInfo field = CollUtil.findOne(fieldList, f -> StrUtil.equals(f.getColumn(), column));
				if (field == null) {
					log.warn("[{}]: 分片字段无效 {}", table, column);
					return dateList;
				}
				Object v = BeanUtil.getFieldValue(parameter, field.getProperty());
				dateList.add((Date) v);
				return dateList;
			}
		}
		// 参数解析
		if (parameter instanceof ParamMap) {
			ParamMap paramMap = (ParamMap) parameter;
			Collection values = paramMap.values();
			for (Object val : values) {
				if (val instanceof AbstractWrapper) {
					AbstractWrapper wrapper = (AbstractWrapper) val;
					MergeSegments segments = wrapper.getExpression();
					if (segments == null || segments.getNormal() == null) {
						continue;
					}
					NormalSegmentList segmentList = segments.getNormal();
					for (int i = 0; i < segmentList.size(); i++) {
						String dbColumn = segmentList.get(i).getSqlSegment();
						if (StrUtil.equalsIgnoreCase(dbColumn, column)) {
							// 匹配到参数
							i++;
							ISqlSegment keyword = CollUtil.get(segmentList, i);
							if (CollUtil.contains(supportSqlKeywords, keyword)) {
								// 获取到值索引
								i++;
								ISqlSegment keySegment = CollUtil.get(segmentList, i);
								// 索引越界
								if (keySegment == null) {
									continue;
								}
								// 构造dateList
								dateList.addAll(getDateByParam(parameter, keySegment));
								// between ? and ? -> (跳2格)
								i = i + 2;
								if (keyword == SqlKeyword.BETWEEN) {
									keySegment = CollUtil.get(segmentList, i);
									dateList.addAll(getDateByParam(parameter, keySegment));
								}
							}
						}
					}
					break;
				}
			}
		}
		return dateList;
	}

	private List<Date> getDateByParam(Object parameter, ISqlSegment keySegment) {
		if (keySegment == null) {
			return new ArrayList<>(0);
		}
		String expression = keySegment.getSqlSegment();
		return CollUtil.map(ReUtil.findAllGroup1(paramRegex, expression), k -> {
			Object v = BeanUtil.getProperty(parameter, k);
			if (v == null) {
				return null;
			}
			if (v instanceof Date) {
				return (Date) v;
			}
			return Convert.convert(Date.class, v);
		}, true);
	}

}