package com.chao.cloud.common.extra.mybatis.config;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.chao.cloud.common.extra.mybatis.common.DBConstant;
import com.chao.cloud.common.extra.mybatis.common.DateStrategyEnum;
import com.chao.cloud.common.extra.mybatis.sharding.ShardingEnum;

import cn.hutool.core.date.DateField;
import cn.hutool.core.map.MapUtil;
import lombok.Data;

/**
 * 表分片的配置
 * 
 * @author 薛超
 * @since 2022年5月23日
 * @version 1.0.0
 */
@Data
@ConfigurationProperties(prefix = DBConstant.SHARDING_CONFIG_PREFIX)
public class ShardingTableProperties {

	/**
	 * 默认不启用
	 */
	private boolean enabled;
	/**
	 * 是否支持分页
	 */
	private boolean supportPage = true;
	/**
	 * 最近的月份数量
	 */
	private int latestMonths = 1;
	/**
	 * 范围下界，超过边界的数据会报错
	 */
	private int datetimeLower = 12;
	/**
	 * 范围上界，超过边界的数据会报错
	 */
	private int datetimeUpper = 1;
	/**
	 * 时间单位
	 */
	private DateField datetimeUnit = DateField.MONTH;
	/**
	 * aop执行顺序
	 */
	private int order = 0;

	/**
	 * 动态表名
	 */
	private Map<String, ShardingTableRule> shardingTableRule = MapUtil.empty();

	@Data
	public static class ShardingTableRule {
		/**
		 * 分片类型（默认不分片）
		 */
		private ShardingEnum type = ShardingEnum.NONE;
		/**
		 * 分片字段（目前只支持单个字段）
		 */
		private String column;
		/**
		 * 日期策略
		 */
		private DateStrategyEnum dateStrategy = DateStrategyEnum.MONTH_12;
	}
}
