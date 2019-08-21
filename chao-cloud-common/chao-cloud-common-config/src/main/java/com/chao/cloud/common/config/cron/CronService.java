package com.chao.cloud.common.config.cron;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.chao.cloud.common.config.cron.task.CronTask;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.cron.CronUtil;
import cn.hutool.log.StaticLog;

/**
 * 定时器 
 * @author 薛超
 * @since 2019年8月21日
 * @version 1.0.7
 */
public interface CronService {

	/**
	 *  回收线程的周期 5分钟
	 */
	String RECOVERY_CRON = "0 */5 * * * ?";

	Map<String, CronTask> CRON_MAP = new ConcurrentHashMap<>();

	static List<CronTask> list() {
		return CollUtil.newArrayList(CRON_MAP.values());
	}

	/**
	 * 加入定时任务
	 * @param task 任务 {@link CronTask}
	 * @return 定时任务ID
	 */
	static String schedule(CronTask task) {
		// 精确定时器
		if (CronTask.EXACT_CRON_TYPE.equals(task.getCronType())) {
			DateTime date = DateUtil.parse(task.getCron(), CronTask.EXACT_CRON_FORMAT);
			// 断言表达式时间
			Assert.state(System.currentTimeMillis() < date.getTime(), "[定时器超过当前时间：id={},date={}]", task.getId(), date);
		}
		//
		CronUtil.schedule(task.getId(), task.getCron(), task);
		CRON_MAP.put(task.getId(), task);
		StaticLog.info("[任务添加成功：id={},cron={}]", task.getId(), task.getCron());
		return task.getId();
	}

	/**
	 * 移除任务
	 * @param id 定时器的ID 
	 */
	static void remove(String id) {
		CRON_MAP.remove(id);
		CronUtil.remove(id);
	}

	/**
	 * 回收线程
	 * @return 清理线程的ID
	 */
	static String recovery() {
		// 5分钟检查
		String check = CronUtil.schedule(RECOVERY_CRON, (Runnable) () -> {
			List<CronTask> list = CronService.list();
			list.forEach(l -> {
				if (CronTask.DEFAULT_CRON_TYPE.equals(l.getCronType())) {
					return;
				}
				Date date = DateUtil.parse(l.getCron(), CronTask.EXACT_CRON_FORMAT);
				if (System.currentTimeMillis() > date.getTime()) {
					CronService.remove(l.getId());
					StaticLog.info("[清理无效线程:id={},date={}]", l.getId(), date);
				}
			});
		});
		StaticLog.info("[定时器检查ID={}]", check);
		return check;
	}

	/**
	 * 时间转cron 表达式
	 * @param date 执行的时间
	 * @return 转换后的cron表达式
	 */
	static String getExactCron(Date date) {
		return DateUtil.format(date, CronTask.EXACT_CRON_FORMAT);
	}

}
