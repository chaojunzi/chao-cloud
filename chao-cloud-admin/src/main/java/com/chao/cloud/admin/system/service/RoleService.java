package com.chao.cloud.admin.system.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.chao.cloud.admin.system.domain.dto.RoleDTO;

@Service
public interface RoleService {

	RoleDTO get(Long id);

	List<RoleDTO> list(Map<String, Object> params);

	int count(Map<String, Object> params);

	int save(RoleDTO role);

	int update(RoleDTO role);

	int remove(Long id);

	List<RoleDTO> list(Long userId, List<Long> rolesIds);

	int batchremove(Long[] ids);
}
