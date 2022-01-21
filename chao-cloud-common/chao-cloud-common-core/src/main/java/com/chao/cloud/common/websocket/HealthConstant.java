package com.chao.cloud.common.websocket;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

import com.chao.cloud.common.web.config.VersionConfig;
import com.chao.cloud.common.web.config.VersionConfig.VersionProperties;
import com.sun.management.OperatingSystemMXBean;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.system.RuntimeInfo;
import cn.hutool.system.SystemUtil;

/**
 * 健康检测
 * 
 * @author 薛超
 * @since 2021年10月26日
 * @version 1.0.0
 */
public interface HealthConstant {

	String IP = NetUtil.getLocalhostStr();
	String MAC = NetUtil.getLocalMacAddress();
	long PID = SystemUtil.getCurrentPID();
	Date START_TIME = new Date();

	AtomicLong prevGetTime = new AtomicLong(System.nanoTime());
	AtomicLong prevCpuUsedTime = new AtomicLong(0);
	/**
	 * 精度
	 */
	int SCALE = 2;
	/**
	 * 统计
	 */
	OperatingSystemMXBean OS_BEAN = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
	/**
	 * 当前版本号
	 */
	String VERSION = "1.0";

	static HealthCoreModel buildHealthCoreModel() {
		RuntimeInfo runtime = SystemUtil.getRuntimeInfo();
		// 分配内存百分比
		long useMemory = runtime.getTotalMemory() - runtime.getFreeMemory();
		String useRate = decimalFormat(NumberUtil.div(useMemory, runtime.getTotalMemory(), SCALE));
		// cpu信息
		BigDecimal cpuLoad = NumberUtil.round(OS_BEAN.getProcessCpuLoad(), SCALE);
		int cpuCount = OS_BEAN.getAvailableProcessors();
		String cpuUsage = decimalFormat(NumberUtil.div(getProcessCpuUsage(), cpuCount * 100, SCALE));
		return HealthCoreModel.of()//
				.setUseRate(useRate)//
				.setUseMemory(FileUtil.readableFileSize(useMemory))//
				.setFreeMemory(FileUtil.readableFileSize(runtime.getFreeMemory()))//
				.setTotalMemory(FileUtil.readableFileSize(runtime.getTotalMemory()))//
				.setThreadCount(SystemUtil.getTotalThreadCount())//
				.setCpuCount(cpuCount)//
				.setCpuLoad(cpuLoad)//
				.setVersion(getVersion())//
				.setCpuUsage(cpuUsage);
	}

	/**
	 * 获取当前进程 cpu 使用量
	 * 
	 * @return cpu 使用量, 如使用2核, 返回200%
	 */
	static double getProcessCpuUsage() {
		ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
		long totalCpuUsedTime = 0;
		for (long id : threadBean.getAllThreadIds()) {
			totalCpuUsedTime += threadBean.getThreadCpuTime(id);
		}
		long curtime = System.nanoTime();
		long usedTime = totalCpuUsedTime - prevCpuUsedTime.get(); // cpu 用时差
		long totalPassedTime = curtime - prevGetTime.get(); // 时间差
		prevGetTime.set(curtime);
		prevCpuUsedTime.set(totalCpuUsedTime);
		return (((double) usedTime) / totalPassedTime) * 100;
	}

	static String decimalFormat(double value) {
		return NumberUtil.decimalFormat("#.##%", value);
	}

	static String buildUrl(String host, int port) {
		return StrUtil.format("http://{}:{}", host, port);
	}

	static boolean isOpen(String host, int port) {
		return isOpen(host, port, 3000);
	}

	static boolean isOpen(String host, int port, int timeout) {
		URL httpUrl = URLUtil.toUrlForHttp(buildUrl(host, port));
		URLConnection conn = null;
		try {
			conn = httpUrl.openConnection();
			conn.setConnectTimeout(timeout);
			return conn.getContentLengthLong() > 0;
		} catch (IOException e) {
			throw new IORuntimeException(e);
		} finally {
			if (conn instanceof HttpURLConnection) {
				((HttpURLConnection) conn).disconnect();
			}
		}
	}

	static String getVersion() {
		VersionProperties properties = VersionConfig.getProperties();
		if (properties == null || StrUtil.isBlank(properties.getVersion())) {
			return VERSION;
		}
		String version = StrUtil.blankToDefault(properties.getVersion(), VERSION);
		// 时间戳
		// Date date = DateUtil.parse(properties.getTimestamp());
		// if (date == null) {
		// date = new Date();
		// }
		// return StrUtil.format("{}（{}）", version, DateUtil.formatChineseDate(date,
		// false, false));
		return StrUtil.format("{}（{}）", version, properties.getTimestamp());
	}

}
