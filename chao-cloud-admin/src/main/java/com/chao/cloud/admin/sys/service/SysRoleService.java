package com.chao.cloud.admin.sys.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chao.cloud.admin.sys.dal.entity.SysRole;
import com.chao.cloud.admin.sys.domain.dto.RoleDTO;

/**
 * @功能：
 * @author： 超君子
 * @时间：2019-05-28
 * @version 1.0.0
 */
public interface SysRoleService extends IService<SysRole> {

	RoleDTO get(Long roleId);

	List<RoleDTO> list(List<Long> rolesIds);

	boolean save(RoleDTO role);

	boolean update(RoleDTO role);

	boolean remove(Long roleId);

	boolean batchRemove(Long[] roleIds);

}
