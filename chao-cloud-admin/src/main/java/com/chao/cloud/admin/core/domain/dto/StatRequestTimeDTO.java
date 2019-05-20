package com.chao.cloud.admin.core.domain.dto;

import java.math.BigDecimal;
import java.util.List;

import com.google.common.collect.Lists;

import lombok.Data;

/**
 * 统计请求时间dto
 * @功能：
 * @author： 薛超
 * @时间：2019年5月17日
 * @version 2.0
 */
@Data
public class StatRequestTimeDTO {

    private List<String> legendData = Lists.newArrayList("最大时间", "平均时间", "最小时间");// 图例。
    private List<String> xAxisData;// 横坐标轴

    private List<BigDecimal> max;// 最大值
    private List<BigDecimal> min;// 最小值
    private List<BigDecimal> avg;// 平均值
}
