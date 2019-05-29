package com.chao.cloud.admin.sys.constant;

public interface AdminConstant {
	String ADMIN = "admin";
	Long ADMIN_ID = 1L;
	// 自动去除表前缀
	String AUTO_REOMVE_PRE = "true";
	// 停止计划任务
	String STATUS_RUNNING_STOP = "stop";
	// 开启计划任务
	String STATUS_RUNNING_START = "start";
	// 通知公告阅读状态-未读
	String OA_NOTIFY_READ_NO = "0";
	// 通知公告阅读状态-已读
	int OA_NOTIFY_READ_YES = 1;
	// 部门根节点id
	Long DEPT_ROOT_ID = 0l;
	// 缓存方式
	String CACHE_TYPE_REDIS = "redis";

	String LOG_ERROR = "error";

}
