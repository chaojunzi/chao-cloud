package com.chao.cloud.common.extra.sharding.strategy;

import java.math.RoundingMode;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import cn.hutool.core.comparator.CompareUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;

/**
 * 日期策略
 * 
 * @author 薛超
 * @since 2020年5月29日
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

	private final int month;

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
	 * @return 表真实节点
	 */
	public String getCurrentTableNode(String logicTableName) {
		// 年月
		String yearMonth = this.convertDateStr(DateUtil.date());
		// 真实节点
		return StrUtil.format("{}_{}", logicTableName, yearMonth);
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
	 * 获取时间的年份和月份然后进行落点
	 * 
	 */
	private String convertDateStr(Date date) {
		if (date == null || DateUtil.compare(date, DateUtil.date()) > 0) {
			return null;
		}
		// 判断该时间是否大于当前时间(不允许查询超出当前时间的表)
		// 获取年份
		int year = DateUtil.year(date);
		// 获取月份
		int mon = DateUtil.month(date) + 1;
		// 根据步长计算落点
		String point = NumberUtil.roundStr(mon / (this.month * 1.0), 0, RoundingMode.UP);
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

}
