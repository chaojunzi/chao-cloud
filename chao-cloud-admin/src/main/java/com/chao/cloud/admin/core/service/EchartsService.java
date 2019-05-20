package com.chao.cloud.admin.core.service;

import java.math.BigDecimal;

import com.chao.cloud.admin.core.domain.dto.StatRequestTimeDTO;

public interface EchartsService {

    /**
     * 保留2位小数
     * @param amount
     * @return
     */
    static BigDecimal persist2PointUp(BigDecimal time) {
        return time.divide(BigDecimal.valueOf(1000.0)).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 统计controller 层 执行时间
     * @param prefix 操作前缀
     * @return
     */
    StatRequestTimeDTO statRequestTime(String prefix);

}
