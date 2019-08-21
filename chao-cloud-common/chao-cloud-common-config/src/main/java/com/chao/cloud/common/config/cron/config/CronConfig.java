package com.chao.cloud.common.config.cron.config;

import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;

import com.chao.cloud.common.config.cron.CronService;
import com.chao.cloud.common.config.cron.task.CronTask;
import com.chao.cloud.common.core.ApplicationOperation;
import com.chao.cloud.common.core.IApplicationRestart;
import com.chao.cloud.common.entity.Response;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.Scheduler;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 定时器管理
 * @功能：
 * @author： 薛超
 * @时间：2019年8月21日
 * @version 1.0.7
 */
@Slf4j
@Configuration
@Getter
public class CronConfig implements IApplicationRestart, InitializingBean {

	private String checkId;

	@Override
	public void afterPropertiesSet() throws Exception {
		// 设置秒启动
		CronUtil.setMatchSecond(true);
		Scheduler scheduler = CronUtil.getScheduler();
		scheduler.clear();
		// 启动定时器
		if (!scheduler.isStarted()) {
			CronUtil.start();
		}
		this.checkId = CronService.recovery();
		log.info("定时器启动成功");
	}

	/**
	 * 加载服务中的定时任务
	 */
	@Override
	public Response<String> restart() {
		//
		List<CronTask> tasks = ApplicationOperation.getInterfaceImplClass(CronTask.class);
		if (CollUtil.isNotEmpty(tasks)) {
			tasks.forEach(t -> CronService.schedule(t));
		}
		log.info("[定时任务数量:{}]", tasks.size());
		return Response.ok();
	}

}