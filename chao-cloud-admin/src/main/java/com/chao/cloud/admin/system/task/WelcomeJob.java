package com.chao.cloud.admin.system.task;

import org.springframework.stereotype.Component;

import cn.hutool.core.date.DateTime;
import cn.hutool.cron.task.Task;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @功能：测试定时器
 * @author： 薛超
 * @时间：2019年3月12日
 * @version 1.0.0
 */
@Component
@Slf4j
public class WelcomeJob implements Task {

    @Override
    public void execute() {
        log.info("定时器：WelcomeJob={}", DateTime.now());
    }

}