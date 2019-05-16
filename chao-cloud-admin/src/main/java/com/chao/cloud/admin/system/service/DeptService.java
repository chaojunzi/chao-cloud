package com.chao.cloud.admin.system.service;

import java.util.List;
import java.util.Map;

import com.chao.cloud.admin.system.domain.dto.DeptDTO;
import com.chao.cloud.admin.system.domain.dto.TreeDTO;

/**
 * 部门管理
 * 
 * @author chglee
 * @email 1992lcg@163.com
 * @date 2017-09-27 14:28:36
 */
public interface DeptService {

    DeptDTO get(Long deptId);

    List<DeptDTO> list(Map<String, Object> map);

    int count(Map<String, Object> map);

    int save(DeptDTO sysDept);

    int update(DeptDTO sysDept);

    int remove(Long deptId);

    int batchRemove(Long[] deptIds);

    TreeDTO<DeptDTO> getTree();

    boolean checkDeptHasUser(Long deptId);

   // List<Long> listChildrenIds(Long parentId);
}
