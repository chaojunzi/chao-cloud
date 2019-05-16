package com.chao.cloud.admin.system.controller;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.chao.cloud.admin.system.domain.dto.TaskDTO;
import com.chao.cloud.admin.system.service.JobService;
import com.chao.cloud.admin.system.utils.Query;
import com.chao.cloud.admin.system.utils.R;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @功能：
 * @author： 薛超
 * @时间：2019年3月13日
 * @version 1.0.0
 */
@Controller
@RequestMapping("/sys/job")
@Slf4j
public class JobController extends BaseController {
    String prefix = "system/job";
    @Autowired
    private JobService taskScheduleJobService;

    @GetMapping()
    String taskScheduleJob() {
        return prefix + "/job";
    }

    @ResponseBody
    @GetMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        // 查询列表数据
        Query query = new Query(params);
        int count = taskScheduleJobService.count(query);
        List<TaskDTO> data = Collections.emptyList();
        if (count > 0) {
            data = taskScheduleJobService.list(query);
        }
        return R.page(data, count);
    }

    @GetMapping("/add")
    String add() {
        return prefix + "/add";
    }

    @GetMapping("/edit/{id}")
    String edit(@PathVariable("id") Long id, Model model) {
        TaskDTO job = taskScheduleJobService.get(id);
        model.addAttribute("job", job);
        return prefix + "/edit";
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id) {
        TaskDTO taskScheduleJob = taskScheduleJobService.get(id);
        return R.ok().put("taskScheduleJob", taskScheduleJob);
    }

    /**
     * 保存
     */
    @ResponseBody
    @PostMapping("/save")
    public R save(TaskDTO taskScheduleJob) {
        if (taskScheduleJobService.save(taskScheduleJob) > 0) {
            return R.ok();
        }
        return R.error();
    }

    /**
     * 修改
     */
    @ResponseBody
    @PostMapping("/update")
    public R update(TaskDTO taskScheduleJob) {
        taskScheduleJobService.update(taskScheduleJob);
        return R.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/remove")
    @ResponseBody
    public R remove(Long id) {
        if (taskScheduleJobService.remove(id) > 0) {
            return R.ok();
        }
        return R.error();
    }

    /**
     * 删除
     */
    @PostMapping("/batchRemove")
    @ResponseBody
    public R remove(@RequestParam("ids[]") Long[] ids) {
        taskScheduleJobService.batchRemove(ids);
        return R.ok();
    }

    @PostMapping(value = "/changeJobStatus")
    @ResponseBody
    public R changeJobStatus(Long id, String cmd) {
        String label = "停止";
        if ("start".equals(cmd)) {
            label = "启动";
        } else {
            label = "停止";
        }
        try {
            taskScheduleJobService.changeStatus(id, cmd);
            return R.ok("任务" + label + "成功");
        } catch (Exception e) {
            log.error("[任务异常：{}]", e);
            return R.error("任务" + label + "失败]--->" + e.getMessage());
        }
    }

}
