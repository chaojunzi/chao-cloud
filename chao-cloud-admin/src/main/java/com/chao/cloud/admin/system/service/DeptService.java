package com.chao.cloud.admin.system.service;

import java.util.List;
import java.util.Map;

import com.chao.cloud.admin.system.domain.dto.DeptDTO;
import com.chao.cloud.admin.system.domain.dto.TreeDTO;

/**
 * 
 * @功能：部门管理
 * @author： 薛超
 * @时间： 2019年5月27日
 * @version 1.0.0
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
