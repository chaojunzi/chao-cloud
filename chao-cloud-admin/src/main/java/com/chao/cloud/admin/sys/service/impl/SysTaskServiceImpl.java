package com.chao.cloud.admin.sys.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chao.cloud.admin.sys.config.CronConfig;
import com.chao.cloud.admin.sys.constant.AdminConstant;
import com.chao.cloud.admin.sys.dal.entity.SysTask;
import com.chao.cloud.admin.sys.dal.mapper.SysTaskMapper;
import com.chao.cloud.admin.sys.service.SysTaskService;
import com.chao.cloud.common.exception.BusinessException;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.pattern.CronPattern;
import cn.hutool.cron.task.Task;

/**
 * @功能：
 * @author： 超君子
 * @时间：2019-05-28
 * @version 1.0.0
 */
@Service 
public class SysTaskServiceImpl extends ServiceImpl<SysTaskMapper, SysTask> implements SysTaskService {

	@Override
	public int remove(Integer id) {
		// 删除任务id
		CronUtil.remove(id.toString());
		return this.baseMapper.deleteById(id);
	}

	@Override
	public int batchRemove(Integer[] ids) {
		for (Integer id : ids) {
			CronUtil.remove(id.toString());
		}
		return this.baseMapper.deleteBatchIds(CollUtil.toList(ids));
	}

	@Override
	public void initSchedule() {
		// 这里获取任务信息数据
		List<SysTask> list = this.list();
		if (CollUtil.isNotEmpty(list)) {
			list.forEach(t -> {
				if (CronConfig.STATUS_RUNNING.equals(t.getJobStatus())
						&& CronConfig.CRON_MAPS.containsKey(t.getBeanClass())) {
					Task task = CronConfig.CRON_MAPS.get(t.getBeanClass());
					CronUtil.schedule(t.getId().toString(), t.getCronExpression(), task);
				}
			});
		}

	}

	@Override
	public void changeStatus(Integer taskId, String cmd) {
		SysTask task = this.baseMapper.selectById(taskId);
		if (BeanUtil.isEmpty(task)) {
			return;
		}
		if (!CronConfig.CRON_MAPS.containsKey(task.getBeanClass())) {
			throw new BusinessException("无效的任务类：" + task.getBeanClass());
		}
		if (AdminConstant.STATUS_RUNNING_STOP.equals(cmd)) {
			task.setJobStatus(CronConfig.STATUS_NOT_RUNNING);
			CronUtil.remove(taskId.toString());
		} else {
			if (!AdminConstant.STATUS_RUNNING_START.equals(cmd)) {
			} else {
				task.setJobStatus(CronConfig.STATUS_RUNNING);
				// 添加一个任务
				Task taskAdd = CronConfig.CRON_MAPS.get(task.getBeanClass());
				CronUtil.schedule(task.getId().toString(), task.getCronExpression(), taskAdd);

			}
		}
		this.baseMapper.updateById(task);
	}

	@Override
	public void updateCron(Integer taskId) {
		SysTask task = this.baseMapper.selectById(taskId);
		if (BeanUtil.isEmpty(task)) {
			return;
		}
		if (!CronConfig.CRON_MAPS.containsKey(task.getBeanClass())) {
			throw new BusinessException("无效的任务类：" + task.getBeanClass());
		}
		if (CronConfig.STATUS_RUNNING.equals(task.getJobStatus())) {
			CronPattern pattern = new CronPattern(task.getCronExpression());
			CronUtil.updatePattern(task.getId().toString(), pattern);
		}
		this.baseMapper.updateById(task);
	}
}
