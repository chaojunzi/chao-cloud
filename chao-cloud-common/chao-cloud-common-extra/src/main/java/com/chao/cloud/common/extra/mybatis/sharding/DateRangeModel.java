package com.chao.cloud.common.extra.mybatis.sharding;

import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.core.enums.SqlKeyword;

import cn.hutool.core.collection.CollUtil;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 日期范围模型
 * 
 * @author 薛超
 * @since 2022年5月18日
 * @version 1.0.0
 */
@Data(staticConstructor = "of")
@Accessors(chain = true)
public class DateRangeModel {

	private SqlKeyword keyword;
	/**
	 * 日期范围
	 */
	private Date lowerDate; // 最小值
	private Date upperDate; // 最大值

	private List<Date> dateList = CollUtil.newArrayList(); // in 或者 eq 取此字段

}
