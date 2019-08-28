package com.chao.cloud.common.config.cron.task;

import cn.hutool.cron.task.Task;

/**
 * 定时任务接口
 * @author 薛超
 * @see <a href="https://www.cnblogs.com/rever/p/9887384.html">cron表达式</a>
 * @since 2019年8月21日
 * @version 1.0.7
 */
public interface CronTask extends Task {
	/**
	 * 精确的定时模板
	 */
	String EXACT_CRON_FORMAT = "ss mm HH dd MM ? yyyy";
	/**
	 * 默认的类型
	 */
	Integer DEFAULT_CRON_TYPE = 0;
	/**
	 * 精确地定时器类型
	 */
	Integer EXACT_CRON_TYPE = 1;

	/**
	 * 获取taskid 自定义 全局唯一
	 * @return taskid
	 */
	String getId();

	/**
	 * cron 表达式 最小单位为秒
	 * @return cron 表达式
	 */
	String getCron();

	/**
	 * 定时器类型
	 * @return 定时器类型 0.任何cron表达式   1.时刻表达式（瞬时时间）
	 */
	default Integer getCronType() {
		return DEFAULT_CRON_TYPE;
	};
}
