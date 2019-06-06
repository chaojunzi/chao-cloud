package com.chao.cloud.admin.core.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.chao.cloud.admin.core.dal.entity.StatRequestTime;
import com.chao.cloud.admin.core.dal.mapper.XcStatLogMapper;
import com.chao.cloud.admin.core.domain.dto.StatRequestTimeDTO;
import com.chao.cloud.admin.core.service.EchartsService;
import com.chao.cloud.admin.sys.log.AdminLog;

@Service
public class EchartsServiceImpl implements EchartsService {

	@Autowired
	private XcStatLogMapper xcStatLogMapper;

	@Override
	public StatRequestTimeDTO statRequestTime(String prefix) {
		StatRequestTimeDTO dto = new StatRequestTimeDTO();
		List<StatRequestTime> list = xcStatLogMapper.statRequestTime(prefix);
		// 数据分组
		List<String> xAxisData = list.stream().map(StatRequestTime::getOperation)
				.map(o -> o.replace(AdminLog.STAT_PREFIX, "")).collect(Collectors.toList());
		List<BigDecimal> max = list.stream().map(StatRequestTime::getMax)
				.map(t -> EchartsService.persist2PointUp(t)).collect(Collectors.toList());
		List<BigDecimal> min = list.stream().map(StatRequestTime::getMin)
				.map(t -> EchartsService.persist2PointUp(t)).collect(Collectors.toList());
		List<BigDecimal> avg = list.stream().map(StatRequestTime::getAvg)
				.map(t -> EchartsService.persist2PointUp(t)).collect(Collectors.toList());
		dto.setXAxisData(xAxisData);
		dto.setAvg(avg);
		dto.setMin(min);
		dto.setMax(max);
		return dto;
	}

}
