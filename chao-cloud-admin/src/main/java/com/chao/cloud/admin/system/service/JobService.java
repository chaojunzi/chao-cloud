package com.chao.cloud.admin.system.service;

import java.util.List;
import java.util.Map;

import com.chao.cloud.admin.system.domain.dto.TaskDTO;

/**
 * 
 * @功能：
 * @author： 薛超
 * @时间：2019年3月13日
 * @version 1.0.0
 */
public interface JobService {

    TaskDTO get(Long id);

    List<TaskDTO> list(Map<String, Object> map);

    int count(Map<String, Object> map);

    int save(TaskDTO taskScheduleJob);

    int update(TaskDTO taskScheduleJob);

    int remove(Long id);

    int batchRemove(Long[] ids);

    void initSchedule() throws Exception;

    void changeStatus(Long jobId, String cmd) throws Exception;

    void updateCron(Long jobId) throws Exception;
}
