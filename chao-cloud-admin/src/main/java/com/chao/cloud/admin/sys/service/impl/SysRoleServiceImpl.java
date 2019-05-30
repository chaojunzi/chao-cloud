package com.chao.cloud.admin.sys.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chao.cloud.admin.sys.dal.entity.SysRole;
import com.chao.cloud.admin.sys.dal.entity.SysRoleMenu;
import com.chao.cloud.admin.sys.dal.mapper.SysRoleMapper;
import com.chao.cloud.admin.sys.domain.dto.RoleDTO;
import com.chao.cloud.admin.sys.service.SysRoleMenuService;
import com.chao.cloud.admin.sys.service.SysRoleService;
import com.chao.cloud.common.util.EntityUtil;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;

/**
 * @功能：
 * @author： 超君子
 * @时间：2019-05-28
 * @version 1.0.0
 */
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

	@Autowired
	private SysRoleMenuService sysRoleMenuService;

	@Override
	public RoleDTO get(Long roleId) {
		RoleDTO role = BeanUtil.toBean(this.baseMapper.selectById(roleId), RoleDTO.class);
		if (!BeanUtil.isEmpty(role)) {
			LambdaQueryWrapper<SysRoleMenu> queryWrapper = Wrappers.<SysRoleMenu>lambdaQuery();
			queryWrapper.select(SysRoleMenu::getMenuId).eq(SysRoleMenu::getRoleId, roleId);
			List<SysRoleMenu> list = sysRoleMenuService.list(queryWrapper);
			if (CollUtil.isNotEmpty(list)) {
				role.setMenuIds(list.stream().map(SysRoleMenu::getMenuId).collect(Collectors.toList()));
			} else {
				role.setMenuIds(Collections.emptyList());
			}
		}
		return role;
	}

	@Override
	public List<RoleDTO> list(List<Long> rolesIds) {
		List<RoleDTO> roles = EntityUtil.listConver(this.baseMapper.selectList(Wrappers.emptyWrapper()), RoleDTO.class);
		if (!CollUtil.isEmpty(rolesIds)) {
			roles.stream().filter(r -> rolesIds.contains(r.getRoleId())).forEach(r -> r.setRoleSign("true"));
		}
		return roles;
	}

	@Transactional
	@Override
	public boolean save(RoleDTO role) {
		SysRole sysRole = BeanUtil.toBean(role, SysRole.class);
		int count = this.baseMapper.insert(sysRole);
		List<Long> menuIds = role.getMenuIds();
		Long roleId = sysRole.getRoleId();
		this.batchSaveRoleMenu(roleId, menuIds);
		sysRoleMenuService.remove(Wrappers.<SysRoleMenu>lambdaQuery().eq(SysRoleMenu::getRoleId, roleId));
		return count > 0;
	}

	@Override
	@Transactional
	public boolean update(RoleDTO role) {
		int r = this.baseMapper.updateById(BeanUtil.toBean(role, SysRole.class));
		List<Long> menuIds = role.getMenuIds();
		Long roleId = role.getRoleId();
		sysRoleMenuService.remove(Wrappers.<SysRoleMenu>lambdaQuery().eq(SysRoleMenu::getRoleId, roleId));
		this.batchSaveRoleMenu(roleId, menuIds);
		return r > 0;
	}

	@Transactional
	@Override
	public boolean remove(Long roleId) {
		int del = this.baseMapper.deleteById(roleId);
		sysRoleMenuService.remove(Wrappers.<SysRoleMenu>lambdaQuery().eq(SysRoleMenu::getRoleId, roleId));
		return del > 0;
	}

	@Override
	@Transactional
	public boolean batchRemove(Long[] roleIds) {
		int del = this.baseMapper.deleteBatchIds(CollUtil.toList(roleIds));
		sysRoleMenuService.remove(Wrappers.<SysRoleMenu>lambdaQuery().in(SysRoleMenu::getRoleId, roleIds));
		return del > 0;
	}

	private void batchSaveRoleMenu(Long roleId, List<Long> menuIds) {
		if (!CollUtil.isEmpty(menuIds)) {
			List<SysRoleMenu> rms = menuIds.stream().filter(l -> ObjectUtil.isNotNull(l)).distinct().map(l -> {
				SysRoleMenu rmDo = new SysRoleMenu();
				rmDo.setRoleId(roleId);
				rmDo.setMenuId(l);
				return rmDo;
			}).collect(Collectors.toList());
			// 再次判断
			if (!CollUtil.isEmpty(rms)) {
				sysRoleMenuService.saveBatch(rms);
			}
		}
	}

}
