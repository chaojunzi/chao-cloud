package com.chao.cloud.admin.sys.config;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import com.chao.cloud.admin.sys.service.SysTaskService;
import com.chao.cloud.common.core.ApplicationOperation;
import com.chao.cloud.common.core.IApplicationRestart;
import com.chao.cloud.common.entity.Response;

import cn.hutool.core.date.DateTime;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.Scheduler;
import cn.hutool.cron.task.Task;
import lombok.extern.slf4j.Slf4j;

/**
 * 定时器管理
 * @功能：
 * @author： 薛超
 * @时间：2019年3月13日
 * @version 1.0.0
 */
@Slf4j
@Configuration
public class CronConfig {

	public static final Map<String, Task> CRON_MAPS = new ConcurrentHashMap<>();
	public static final String STATUS_RUNNING = "1";
	public static final String STATUS_NOT_RUNNING = "0";
	public static final String CONCURRENT_IS = "1";
	public static final String CONCURRENT_NOT = "0";

	public void init() throws Exception {
		List<Task> tasks = ApplicationOperation.getInterfaceImplClass(Task.class);
		if (!tasks.isEmpty()) {
			tasks.forEach(t -> {
				// 增加任务对象
				String key = t.getClass().getName();
				log.info("[init--->cronMaps : {}]", key);
				CRON_MAPS.put(key, t);
			});
		}
	}

	@Component
	class CronTaskInit implements IApplicationRestart {

		@Autowired
		private SysTaskService sysTaskService;

		@Autowired
		private CronConfig cronConfig;

		@Override
		public Response<?> restart() {
			try {
				// 设置秒启动
				CronUtil.setMatchSecond(true);
				Scheduler scheduler = CronUtil.getScheduler();
				scheduler.clear();
				// 获取类
				cronConfig.init();
				// 装载定时器
				sysTaskService.initSchedule();
				// 启动定时器
				if (!scheduler.isStarted()) {
					CronUtil.start();
				}
				log.info("定时器启动成功");

			} catch (Exception e) {
				log.error("[初始化失败：]", e);
			}
			return null;
		}

	}

	/**
	 * 
	 * @功能：测试定时器
	 * @author： 薛超
	 * @时间：2019年3月12日
	 * @version 1.0.0
	 */
	@Component
	public static class WelcomeJob implements Task {

		@Override
		public void execute() {
			log.info("定时器：WelcomeJob={}", DateTime.now());
		}

	}

}
