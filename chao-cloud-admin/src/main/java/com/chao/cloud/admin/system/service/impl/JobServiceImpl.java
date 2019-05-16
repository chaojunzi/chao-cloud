package com.chao.cloud.admin.system.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.chao.cloud.admin.system.config.CronConfig;
import com.chao.cloud.admin.system.constant.AdminConstant;
import com.chao.cloud.admin.system.domain.dto.TaskDTO;
import com.chao.cloud.admin.system.mapper.TaskMapper;
import com.chao.cloud.admin.system.service.JobService;
import com.chao.cloud.common.exception.BusinessException;

import cn.hutool.cron.CronUtil;
import cn.hutool.cron.pattern.CronPattern;
import cn.hutool.cron.task.Task;

@Service
public class JobServiceImpl implements JobService {

    @Autowired
    private TaskMapper taskScheduleJobMapper;

    @Override
    public TaskDTO get(Long id) {
        return taskScheduleJobMapper.get(id);
    }

    @Override
    public List<TaskDTO> list(Map<String, Object> map) {
        return taskScheduleJobMapper.list(map);
    }

    @Override
    public int count(Map<String, Object> map) {
        return taskScheduleJobMapper.count(map);
    }

    @Override
    public int save(TaskDTO taskScheduleJob) {
        return taskScheduleJobMapper.save(taskScheduleJob);
    }

    @Override
    public int update(TaskDTO taskScheduleJob) {
        return taskScheduleJobMapper.update(taskScheduleJob);
    }

    @Override
    public int remove(Long id) {
        try {
            // 删除任务id
            CronUtil.remove(id.toString());
            return taskScheduleJobMapper.remove(id);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }

    }

    @Override
    public int batchRemove(Long[] ids) {
        for (Long id : ids) {
            try {
                CronUtil.remove(id.toString());
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
        }
        return taskScheduleJobMapper.batchRemove(ids);
    }

    @Override
    public void initSchedule() throws Exception {
        // 这里获取任务信息数据
        List<TaskDTO> jobList = taskScheduleJobMapper.list(new HashMap<String, Object>(16));
        for (TaskDTO job : jobList) {
            if (CronConfig.STATUS_RUNNING.equals(job.getJobStatus())
                    && CronConfig.CRON_MAPS.containsKey(job.getBeanClass())) {
                Task task = CronConfig.CRON_MAPS.get(job.getBeanClass());
                CronUtil.schedule(job.getId().toString(), job.getCronExpression(), task);
            }

        }
    }

    @Override
    public void changeStatus(Long jobId, String cmd) throws Exception {
        TaskDTO job = get(jobId);
        if (job == null) {
            return;
        }
        if (!CronConfig.CRON_MAPS.containsKey(job.getBeanClass())) {
            throw new BusinessException("无效的任务类：" + job.getBeanClass());
        }
        if (AdminConstant.STATUS_RUNNING_STOP.equals(cmd)) {
            job.setJobStatus(CronConfig.STATUS_NOT_RUNNING);
            CronUtil.remove(jobId.toString());
        } else {
            if (!AdminConstant.STATUS_RUNNING_START.equals(cmd)) {
            } else {
                job.setJobStatus(CronConfig.STATUS_RUNNING);
                // 添加一个任务
                Task task = CronConfig.CRON_MAPS.get(job.getBeanClass());
                CronUtil.schedule(job.getId().toString(), job.getCronExpression(), task);

            }
        }
        update(job);
    }

    @Override
    public void updateCron(Long jobId) throws Exception {
        TaskDTO job = get(jobId);
        if (job == null) {
            return;
        }
        if (!CronConfig.CRON_MAPS.containsKey(job.getBeanClass())) {
            throw new BusinessException("无效的任务类：" + job.getBeanClass());
        }
        if (CronConfig.STATUS_RUNNING.equals(job.getJobStatus())) {
            CronPattern pattern = new CronPattern(job.getCronExpression());
            CronUtil.updatePattern(job.getId().toString(), pattern);
        }
        update(job);
    }

}
