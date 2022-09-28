package com.chao.cloud.common.extra.mybatis.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.conditions.AbstractWrapper;
import com.baomidou.mybatisplus.core.conditions.ISqlSegment;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.segments.MergeSegments;
import com.baomidou.mybatisplus.core.conditions.segments.NormalSegmentList;
import com.baomidou.mybatisplus.core.enums.SqlKeyword;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.chao.cloud.common.extra.mybatis.common.ApplySql;
import com.chao.cloud.common.extra.mybatis.common.ApplySql.ApplySqlSegment;
import com.chao.cloud.common.extra.mybatis.common.DateStrategyEnum;
import com.chao.cloud.common.extra.mybatis.config.ShardingTableProperties;
import com.chao.cloud.common.extra.mybatis.config.ShardingTableProperties.ShardingTableRule;
import com.chao.cloud.common.extra.mybatis.sharding.DateRangeModel;
import com.chao.cloud.common.extra.mybatis.sharding.ShardingEnum;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;

/**
 * 条件工具类
 * 
 * @author 薛超
 * @since 2021年5月7日
 * @version 1.0.0
 */
public interface WrapperUtil {

	/**
	 * 参数正则<br>
	 * #{ew.paramNameValuePairs.MPGENVAL4}
	 */
	String paramRegex = "#\\{([\\w\\.]+)\\}";
	/**
	 * = ?<br>
	 * in (?,...) <br>
	 * between ? and ?<br>
	 * >= ?<br>
	 * > ?<br>
	 * <= ? <br>
	 * < ?
	 */
	List<SqlKeyword> supportSqlKeywords = CollUtil.toList(//
			SqlKeyword.EQ, //
			SqlKeyword.IN, SqlKeyword.BETWEEN, //
			SqlKeyword.GE, //
			SqlKeyword.GT, //
			SqlKeyword.LE, //
			SqlKeyword.LT);
	/**
	 * 精确查询
	 */
	List<SqlKeyword> exactSqlKeywords = CollUtil.toList(//
			SqlKeyword.EQ, //
			SqlKeyword.IN);

	/**
	 * 日期前后各延长一个月
	 * 
	 * @param <T>  实体类型
	 * @param w    条件构造器
	 * @param func 表达式
	 * @param date 时间
	 * @return 条件构造器
	 */
	static <T> LambdaQueryWrapper<T> betweenDateOneMonth(LambdaQueryWrapper<T> w, SFunction<T, Date> sFunc, Date date) {
		return w.between(sFunc, DateUtil.offsetMonth(date, -1), DateUtil.offsetMonth(date, 1));
	}

	/**
	 * 慎用此方法
	 * 
	 * @param <T>         实体类型
	 * @param wrapper     包装器
	 * @param entityClass 实体类型
	 * @return 包装时间后的查询器
	 */
	static <T> QueryWrapper<T> wrapBetweenDate(QueryWrapper<T> wrapper, Class<T> entityClass) {
		ShardingTableProperties tableRuleProperties = null;
		try {
			tableRuleProperties = SpringUtil.getBean(ShardingTableProperties.class);
		} catch (Exception e) {
			// 忽略
			return wrapper;
		}
		// 获取表名
		String tableName = AnnotationUtil.getAnnotation(entityClass, TableName.class).value();
		// 获取表分片规则
		DateStrategyEnum dateStrategy = null;
		String column = null;
		Map<String, ShardingTableRule> shardingTableRule = tableRuleProperties.getShardingTableRule();
		if (shardingTableRule.containsKey(tableName)) {
			ShardingTableRule rule = shardingTableRule.get(tableName);
			ShardingEnum type = rule.getType();
			if (type == ShardingEnum.DATE) {
				dateStrategy = rule.getDateStrategy();
				column = rule.getColumn();
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

	/**
	 * 解析wrapper中日期类型
	 * 
	 * @return
	 */
	@SuppressWarnings({ "rawtypes" })
	static DateRangeModel parseDateRange(AbstractWrapper wrapper, String column, Object parameter) {
		MergeSegments segments = wrapper.getExpression();
		if (segments == null || segments.getNormal() == null) {
			return null;
		}
		// 这行不写容易造成参数日期获取不到
		segments.getSqlSegment();
		//
		DateRangeModel model = DateRangeModel.of();
		List<Date> dateList = model.getDateList();
		//
		NormalSegmentList segmentList = segments.getNormal();
		for (int i = 0; i < segmentList.size(); i++) {
			String dbColumn = segmentList.get(i).getSqlSegment();
			if (StrUtil.equalsIgnoreCase(dbColumn, column)) {
				// 匹配到参数
				i++;
				ISqlSegment keyword = CollUtil.get(segmentList, i);
				if (CollUtil.contains(WrapperUtil.supportSqlKeywords, keyword)) {
					// 获取到值索引
					i++;
					ISqlSegment keySegment = CollUtil.get(segmentList, i);
					// 索引越界
					if (keySegment == null) {
						continue;
					}
					List<Date> dateValues = getDateByParam(parameter, keySegment);
					switch ((SqlKeyword) keyword) {
					case IN:
					case EQ:
						if (CollUtil.isNotEmpty(dateValues)) {
							model.setKeyword((SqlKeyword) keyword);
							// 构造dateList
							dateList.addAll(dateValues);
						}
						break;
					case GE:
					case GT:
						// 构造dateList
						Date lower = CollUtil.getFirst(dateValues);
						if (lower != null) {
							model.setLowerDate(lower);
							model.setKeyword(SqlKeyword.GE);
						}
						break;
					case LE:
					case LT:
						// 构造dateList
						Date upper = CollUtil.getFirst(dateValues);
						if (upper != null) {
							model.setUpperDate(upper);
							model.setKeyword(SqlKeyword.LE);
						}
						break;
					case BETWEEN:
						Date start = CollUtil.getFirst(dateValues);
						if (start != null) {
							model.setKeyword(SqlKeyword.BETWEEN);
							model.setLowerDate(start);
							// between ? and ? -> (跳2格)
							i = i + 2;
							keySegment = CollUtil.get(segmentList, i);
							Date end = CollUtil.getFirst(getDateByParam(parameter, keySegment));
							if (start != null) {
								model.setUpperDate(end);
							}
						}
						break;
					default:
						break;
					}
				}
			}
		}
		return model;
	}

	static List<Date> getDateByParam(Object parameter, ISqlSegment keySegment) {
		if (keySegment == null) {
			return new ArrayList<>(0);
		}
		String expression = keySegment.getSqlSegment();
		List<String> paramKeys = ReUtil.findAllGroup1(paramRegex, expression);
		List<Date> dateList = buildDateList(paramKeys, parameter);
		if (CollUtil.isEmpty(dateList)) {
			paramKeys = CollUtil.map(paramKeys, k -> StrUtil.subAfter(k, StrUtil.DOT, true), true);
			dateList = buildDateList(paramKeys, parameter);
		}
		return dateList;
	}

	static List<Date> buildDateList(List<String> paramKeys, Object parameter) {
		return CollUtil.map(paramKeys, k -> {
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

	static String getProperty(String column, TableInfo tableInfo) {
		Map<String, String> columnPropMap = tableInfo.getFieldList().stream()
				.collect(Collectors.toMap(TableFieldInfo::getColumn, TableFieldInfo::getProperty));
		String property = columnPropMap.get(column);
		Assert.notBlank(property, "无效的列名映射 column={}", column);
		return property;
	}

	/**
	 * 遍历所有字段并增加别名
	 * 
	 * @param sql   自定义的sql
	 * @param alias 别名：如t1等
	 * @return 包装后的sql
	 */
	static <T> String wrapApplySql(String sql, String alias, Class<T> entityType) {
		if (StrUtil.isBlank(alias) || entityType == null) {
			return sql;
		}
		// 获取属性
		TableInfo tableInfo = FunctionUtil.buildTableInfo(entityType);
		List<String> columnList = CollUtil.map(tableInfo.getFieldList(), TableFieldInfo::getColumn, true);
		if (CollUtil.isEmpty(columnList)) {
			return sql;
		}
		// 遍历替换
		String regex = "(" + CollUtil.join(columnList, "|") + ")";
		// ReUtil.repla
		String finalSql = ReUtil.replaceAll(sql, regex, alias + StrUtil.DOT + "$1");
		return finalSql;
	}

	static <T> ApplySql wrapApplySql(ApplySql applySql, String alias, Class<T> entityType) {
		ApplySqlSegment normalSegment = applySql.getNormalSegment();
		String sql = normalSegment.getSqlSegment();
		if (StrUtil.isNotBlank(sql)) {
			sql = wrapApplySql(sql, alias, entityType);
			normalSegment.setSqlSegment(sql);
		}
		return applySql;

	}
}
