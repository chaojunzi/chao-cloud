package com.chao.cloud.common.extra.mybatis.common;

import java.math.RoundingMode;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chao.cloud.common.extra.mybatis.dynamic.DynamicTableRuleProperties;
import com.chao.cloud.common.extra.mybatis.dynamic.DynamicTableRuleProperties.ShardingTableRule;
import com.chao.cloud.common.extra.mybatis.dynamic.DynamicTableRuleProperties.TableRule;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.comparator.CompareUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 根据日期分片策略
 * 
 * @author 薛超
 * @since 2021年12月20日
 * @version 1.0.0
 */
@RequiredArgsConstructor
public enum DateStrategyEnum {

	/**
	 * 一个月
	 */
	MONTH_1(1),
	/**
	 * 2个月
	 */
	MONTH_2(2),
	/**
	 * 3个月
	 */
	MONTH_3(3),
	/**
	 * 4个月
	 */
	MONTH_4(4),
	/**
	 * 6个月
	 */
	MONTH_6(6),
	/**
	 * 12个月为一年
	 */
	MONTH_12(12);

	public static final String MONTH_PATTERN = "yyyyMM";

	@Getter
	private final int month;

	private String rule;

	/**
	 * 获取要查询的表名
	 * 
	 * @param tableNames 表集合
	 * @param dates      日期
	 * @return 表集合
	 */
	public List<String> findTables(Collection<String> tableNames, Collection<Date> dates) {
		Set<String> dateSet = dates.stream().map(this::convertDateStr).collect(Collectors.toSet());
		return tableNames.stream().filter(t -> dateSet.contains(StrUtil.subSufByLength(t, MONTH_PATTERN.length())))
				.collect(Collectors.toList());
	}

	/**
	 * 获取要查询的表名<br>
	 * 范围比较-自然排序
	 * 
	 * @param tableNames 表集合
	 * @param startDate  开始日期
	 * @param endDate    结束日期
	 * @return 表集合
	 */
	public List<String> findTables(Collection<String> tableNames, Date startDate, Date endDate) {
		Date now = DateUtil.date();
		// 开始日期
		if (startDate != null && DateUtil.compare(startDate, now) > 0) {
			return Collections.emptyList();
		}
		String start = convertDateStr(startDate);
		// 结束日期(结束日期为null，取当前时间)
		if (endDate == null || DateUtil.compare(endDate, now) > 0) {
			endDate = now;
		}
		String end = convertDateStr(endDate);
		// 比较大小通过自然排序处理
		return tableNames.stream().filter(t -> {
			boolean blankStart = StrUtil.isBlank(start);
			boolean blankEnd = StrUtil.isBlank(end);
			if (blankStart && blankEnd) {// 没传日期
				return true;
			}
			String dateStr = StrUtil.subSufByLength(t, MONTH_PATTERN.length());
			if (!blankStart && !blankEnd) {// 都不为空
				return compare(dateStr, start) && compare(end, dateStr);
			}
			if (!blankStart && blankEnd) {// 开始时间不为空
				return compare(dateStr, start);
			}
			if (blankStart && !blankEnd) {// 结束时间不为空
				return compare(end, dateStr);
			}
			return false;
		}).collect(Collectors.toList());
	}

	/**
	 * 获取当前表节点
	 * 
	 * @param logicTableName 逻辑表
	 * @param columnVal      日期条件
	 * @return 表真实节点
	 */
	public String getCurrentTableNode(String logicTableName, Date columnVal) {
		// 年月
		String yearMonth = this.convertDateStr(DateUtil.date());
		// 日期在同一区间
		if (StrUtil.equals(yearMonth, this.convertDateStr(columnVal))) {
			// 真实节点
			return StrUtil.format("{}_{}", logicTableName, yearMonth);
		}
		//
		return null;
	}

	/**
	 * 获取当前表节点
	 * 
	 * @param logicTableName 逻辑表
	 * @return 表真实节点
	 */
	public String getCurrentTableNode(String logicTableName) {
		return buildTableNode(logicTableName, DateUtil.date());
	}

	public String buildTableNode(String logicTableName, Date date) {
		// 年月
		String yearMonth = this.convertDateStr(date);
		if (StrUtil.isBlank(yearMonth)) {
			return null;
		}
		// 真实节点
		return StrUtil.format("{}_{}", logicTableName, yearMonth);
	}

	public String formatDateArray(Date... dateArray) {
		if (ArrayUtil.isAllNull(dateArray)) {
			return StrUtil.EMPTY;
		}
		return formatDateList(CollUtil.toList(dateArray));
	}

	public String formatDateList(List<Date> dateList) {
		if (CollUtil.isEmpty(dateList)) {
			return StrUtil.EMPTY;
		}
		return CollUtil.join(CollUtil.map(dateList, DateUtil::formatDateTime, true), StrUtil.COMMA);
	}

	/**
	 * 获取区间规范
	 * 
	 * @return 描述
	 */
	public String getIntervalRule() {
		if (StrUtil.isNotBlank(rule)) {
			return rule;
		}
		if (month == 1) {
			rule = "自然月";
			return rule;
		}
		List<String> ruleList = CollUtil.toList();
		for (int i = 1; i < 12; i++) {
			int end = i + month - 1;
			ruleList.add(StrUtil.format("{}~{}月", i, end));
			i = end;
		}
		rule = CollUtil.join(ruleList, StrUtil.COMMA);
		return rule;
	}

	public int hitPoint(Date date) {
		// 获取月份
		int mon = DateUtil.month(date) + 1;
		// 根据步长计算落点
		int point = NumberUtil.round(mon / (this.month * 1.0), 0, RoundingMode.UP).intValue();
		return point;
	}

	/**
	 * 根据年月构造日期
	 * 
	 * @param year  年份
	 * @param month 月份
	 * @param right 是否为右->当月最后一刻
	 * @return 日期
	 */
	public Date buildDateByYearAndMonth(int year, int month, boolean right) {
		// 获取年份
		DateTime date = DateUtil.parse(StrUtil.format("{}-{}", //
				year, //
				StrUtil.padPre(month + "", 2, '0')), //
				"yyyy-MM");
		if (right) {
			return DateUtil.endOfMonth(date);
		}
		return date;
	}

	public Date buildDateByYearAndMonth(int year, int month) {
		return buildDateByYearAndMonth(year, month, false);
	}

	/**
	 * 获取时间的年份和月份然后进行落点
	 * 
	 */
	private String convertDateStr(Date date) {
		// 获取年份
		int year = DateUtil.year(date);
		// 根据步长计算落点
		String point = StrUtil.toString(hitPoint(date));
		// 返回
		return StrUtil.format("{}{}", year, StrUtil.padPre(point, 2, '0'));
	}

	/**
	 * 比较d1和d2 <br>
	 *
	 * @param d1
	 * @param d2
	 * @return d1>=d2 为true 反之为false
	 */
	private boolean compare(String d1, String d2) {
		return CompareUtil.compare(d1, d2) >= 0;
	}

	public <T> QueryWrapper<T> buildQueryWrapper(QueryWrapper<T> wrapper, Class<T> entityType,
			DynamicTableRuleProperties tableRuleProperties) {
		// 获取表名
		String tableName = AnnotationUtil.getAnnotation(entityType, TableName.class).value();
		// 获取表分片规则
		DateStrategyEnum dateStrategy = null;
		String column = null;
		Map<String, TableRule> datasource = tableRuleProperties.getDatasource();
		for (TableRule tableRule : datasource.values()) {
			Map<String, ShardingTableRule> shardingTableRule = tableRule.getShardingTableRule();
			if (shardingTableRule.containsKey(tableName)) {
				ShardingTableRule rule = shardingTableRule.get(tableName);
				ShardEnum type = rule.getType();
				if (type == ShardEnum.DATE) {
					dateStrategy = rule.getDateStrategy();
					column = rule.getColumn();
				}
				break;
			}
		}
		if (ArrayUtil.hasNull(dateStrategy, column)) {
			return wrapper;
		}
		// 获取时间临界值
		DateTime current = DateUtil.date();
		// 区间
		int p = dateStrategy.hitPoint(current);
		// m个月一张表
		int m = dateStrategy.getMonth();
		// 计算右边界
		int right = p * m;
		// 计算左边界
		int left = right - m + 1;
		// 获取年份
		int year = DateUtil.year(current);
		Date startDate = dateStrategy.buildDateByYearAndMonth(year, left);
		Date endDate = dateStrategy.buildDateByYearAndMonth(year, right, true);
		return wrapper.between(column, startDate, endDate);
	}

	public static void main(String[] args) {
		DateStrategyEnum s = DateStrategyEnum.MONTH_12;
		DateTime current = DateUtil.parse("2022-01-01");
		int p = s.hitPoint(current);
		System.out.println("p:" + p);
		int m = s.getMonth();
		// 区间
		// 初始月份
		// 计算左边界
		int right = p * m;
		int left = right - m + 1;

		int year = DateUtil.year(current);
		Date startDate = s.buildDateByYearAndMonth(year, left);
		Date endDate = s.buildDateByYearAndMonth(year, right, true);
		System.out.println(startDate + "~" + endDate);
	}

}
