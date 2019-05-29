package com.chao.cloud.admin.core.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.chao.cloud.admin.core.domain.dto.StatRequestTimeDTO;
import com.chao.cloud.admin.core.service.EchartsService;
import com.chao.cloud.admin.sys.log.AdminLog;
import com.chao.cloud.common.entity.Response;
import com.chao.cloud.common.entity.ResponseResult;

/**
 * echarts绘图
 * @功能：
 * @author： 薛超
 * @时间：2019年5月16日
 * @version 2.0
 */
@Controller
@RequestMapping("/echarts")
public class EchartsController {

	@Autowired 
	private EchartsService echartsService;

	/**
	  *    统计请求时间 
	 * @param url
	 * @return
	 */
	@ResponseBody
	@RequestMapping("statRequestTime")
	public Response<StatRequestTimeDTO> statRequestTime() {
		StatRequestTimeDTO dto = echartsService.statRequestTime(AdminLog.STAT_PREFIX);
		return ResponseResult.getResponseResult(dto);
	}

}
