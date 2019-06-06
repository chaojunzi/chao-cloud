package com.chao.cloud.admin.core.dal.entity;

import java.math.BigDecimal;

import lombok.Data;

/**
 * 统计请求时间dto
 * @功能：
 * @author： 薛超
 * @时间：2019年5月17日
 * @version 2.0
 */
@Data
public class StatRequestTime {

    private String operation;// 操作
    private BigDecimal max;// 操作
    private BigDecimal min;// 操作
    private BigDecimal avg;// 操作
}
