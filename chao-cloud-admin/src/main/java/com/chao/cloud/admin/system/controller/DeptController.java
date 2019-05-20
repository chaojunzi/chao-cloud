package com.chao.cloud.admin.system.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.chao.cloud.admin.system.annotation.AdminLog;
import com.chao.cloud.admin.system.constant.AdminConstant;
import com.chao.cloud.admin.system.domain.dto.DeptDTO;
import com.chao.cloud.admin.system.domain.dto.TreeDTO;
import com.chao.cloud.admin.system.service.DeptService;
import com.chao.cloud.admin.system.utils.R;

/**
 * 部门管理 
 * @功能：
 * @author： 薛超
 * @时间：2019年5月9日
 * @version 2.0
 */
@Controller
@RequestMapping("/sys/dept")
public class DeptController extends BaseController {
    private String prefix = "system/dept";
    @Autowired
    private DeptService deptService;

    @GetMapping()
    @RequiresPermissions("system:sysDept:sysDept")
    String dept() {
        return prefix + "/dept";
    }

    @AdminLog(AdminLog.STAT_PREFIX + "部门列表")
    @ResponseBody
    @GetMapping("/list")
    @RequiresPermissions("system:sysDept:sysDept")
    public R list() {
        Map<String, Object> query = new HashMap<>(16);
        List<DeptDTO> depts = deptService.list(query);
        return R.page(depts);
    }

    @GetMapping("/choose")
    String deptChoose() {
        return prefix + "/choose";
    }

    @GetMapping("/add/{pId}")
    @RequiresPermissions("system:sysDept:add")
    String add(@PathVariable("pId") Long pId, Model model) {
        model.addAttribute("pId", pId);
        if (pId == 0) {
            model.addAttribute("pName", "总部门");
        } else {
            model.addAttribute("pName", deptService.get(pId).getName());
        }
        return prefix + "/add";
    }

    @GetMapping("/edit/{deptId}")
    @RequiresPermissions("system:sysDept:edit")
    String edit(@PathVariable("deptId") Long deptId, Model model) {
        DeptDTO sysDept = deptService.get(deptId);
        model.addAttribute("sysDept", sysDept);
        if (AdminConstant.DEPT_ROOT_ID.equals(sysDept.getParentId())) {
            model.addAttribute("parentDeptName", "无");
        } else {
            DeptDTO parDept = deptService.get(sysDept.getParentId());
            model.addAttribute("parentDeptName", parDept.getName());
        }
        return prefix + "/edit";
    }

    /**
     * 保存
     */
    @ResponseBody
    @PostMapping("/save")
    @RequiresPermissions("system:sysDept:add")
    public R save(DeptDTO sysDept) {
        if (deptService.save(sysDept) > 0) {
            return R.ok();
        }
        return R.error();
    }

    /**
     * 修改
     */
    @ResponseBody
    @RequestMapping("/update")
    @RequiresPermissions("system:sysDept:edit")
    public R update(DeptDTO sysDept) {
        if (deptService.update(sysDept) > 0) {
            return R.ok();
        }
        return R.error();
    }

    /**
     * 删除
     */
    @PostMapping("/remove")
    @ResponseBody
    @RequiresPermissions("system:sysDept:remove")
    public R remove(Long deptId) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("parentId", deptId);
        if (deptService.count(map) > 0) {
            return R.error(1, "包含下级部门,不允许修改");
        }
        if (deptService.checkDeptHasUser(deptId)) {
            if (deptService.remove(deptId) > 0) {
                return R.ok();
            }
        } else {
            return R.error(1, "部门包含用户,不允许修改");
        }
        return R.error();
    }

    /**
     * 删除
     */
    @PostMapping("/batchRemove")
    @ResponseBody
    @RequiresPermissions("system:sysDept:batchRemove")
    public R remove(@RequestParam("ids[]") Long[] deptIds) {
        deptService.batchRemove(deptIds);
        return R.ok();
    }

    @GetMapping("/tree")
    @ResponseBody
    public TreeDTO<DeptDTO> tree() {
        TreeDTO<DeptDTO> tree = new TreeDTO<DeptDTO>();
        tree = deptService.getTree();
        return tree;
    }

    @GetMapping("/treeView")
    String treeView() {
        return prefix + "/deptTree";
    }

}
