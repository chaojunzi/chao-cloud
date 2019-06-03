package com.chao.cloud.admin.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chao.cloud.admin.sys.dal.entity.SysTask;

/**
 * @功能：
 * @author： 超君子
 * @时间：2019-05-28
 * @version 1.0.0
 */
public interface SysTaskService extends IService<SysTask> {

	int remove(Integer id);

	int batchRemove(Integer[] ids);

	void initSchedule();

	void changeStatus(Integer taskId, String cmd);

	void updateCron(Integer taskId);

}
