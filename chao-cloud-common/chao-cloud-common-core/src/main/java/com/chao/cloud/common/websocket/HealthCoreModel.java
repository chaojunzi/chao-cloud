package com.chao.cloud.common.websocket;

import java.math.BigDecimal;
import java.util.Date;

import com.chao.cloud.common.constant.StatusConstant;

import lombok.Data;
import lombok.experimental.Accessors;

@Data(staticConstructor = "of")
@Accessors(chain = true)
public class HealthCoreModel {

	private String ip = HealthConstant.IP;
	private String macAddress = HealthConstant.MAC;
	private long pid = HealthConstant.PID;
	// 百分比
	private String useRate;// 使用率
	// jvm 内存
	private String useMemory;// 使用内存
	private String freeMemory;// 剩余内存
	private String totalMemory;// 总内存
	// 当前进程使用的线程数
	private int threadCount;// 线程数
	// cpu总数
	private int cpuCount;
	// jvm-cpu负载
	private BigDecimal cpuLoad;
	// jvm-cpu平均使用率
	private String cpuUsage;
	// 启动时间
	private Date startTime = HealthConstant.START_TIME;
	// 运行时间
	private String uptime = StatusConstant.WEAK;
	//
	private String version = HealthConstant.getVersion();

}