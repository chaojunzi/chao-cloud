package com.chao.cloud.common.config.web;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chao.cloud.common.entity.Response;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.system.RuntimeInfo;
import cn.hutool.system.SystemUtil;
import lombok.Data;

/**
 * 
 * @功能：健康检查
 * @author： 薛超
 * @时间： 2019年7月19日
 * @version 1.0.2
 */
@RestController
@RequestMapping("health")
public class HealthController {

	private static int scale = 2;// 保留2位小数

	@RequestMapping("/core")
	public Response<CoreParam> core() {
		return Response.ok(coreParam());
	}

	public static CoreParam coreParam() {
		RuntimeInfo runtime = SystemUtil.getRuntimeInfo();
		// 分配内存百分比
		long useMemory = runtime.getTotalMemory() - runtime.getFreeMemory();
		String useRate = decimalFormat(NumberUtil.div(useMemory, runtime.getTotalMemory(), scale));
		//
		CoreParam coreParam = new CoreParam();
		coreParam.setUseRate(useRate);
		coreParam.setUseMemory(FileUtil.readableFileSize(useMemory));
		coreParam.setFreeMemory(FileUtil.readableFileSize(runtime.getFreeMemory()));
		coreParam.setTotalMemory(FileUtil.readableFileSize(runtime.getTotalMemory()));
		coreParam.setThreadCount(SystemUtil.getTotalThreadCount());
		coreParam.setIp(NetUtil.getLocalhostStr());
		coreParam.setMacAddress(NetUtil.getLocalMacAddress());
		return coreParam;
	}

	private static String decimalFormat(double value) {
		return NumberUtil.decimalFormat("#.##%", value);
	}

	/**
	 * 核心参数
	 */
	@Data
	public static class CoreParam {
		private String ip;
		private String macAddress;
		// 百分比
		private String useRate;// 使用率
		// jvm 内存
		private String useMemory;// 使用内存
		private String freeMemory;// 剩余内存
		private String totalMemory;// 总内存
		// 线程数
		private int threadCount;// 线程数
	}

}
