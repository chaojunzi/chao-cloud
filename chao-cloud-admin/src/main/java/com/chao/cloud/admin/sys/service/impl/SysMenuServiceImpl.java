package com.chao.cloud.admin.sys.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chao.cloud.admin.sys.constant.AdminConstant;
import com.chao.cloud.admin.sys.dal.entity.SysMenu;
import com.chao.cloud.admin.sys.dal.entity.SysRoleMenu;
import com.chao.cloud.admin.sys.dal.entity.SysUserRole;
import com.chao.cloud.admin.sys.dal.mapper.SysMenuMapper;
import com.chao.cloud.admin.sys.dal.mapper.SysRoleMenuMapper;
import com.chao.cloud.admin.sys.dal.mapper.SysUserRoleMapper;
import com.chao.cloud.admin.sys.domain.dto.MenuLayuiDTO;
import com.chao.cloud.admin.sys.service.SysMenuService;
import com.chao.cloud.admin.sys.shiro.ShiroUtils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;

/**
 * @功能：
 * @author： 超君子
 * @时间：2019-05-28
 * @version 1.0.0
 */
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

	@Autowired
	private SysRoleMenuMapper sysRoleMenuMapper;
	@Autowired
	private SysUserRoleMapper sysUserRoleMapper;

	@Override
	public Set<String> listPerms(Long userId) {
		LambdaQueryWrapper<SysMenu> queryWrapper = Wrappers.<SysMenu>lambdaQuery().select(SysMenu::getPerms);
		List<SysMenu> menus = listUserMenu(userId, queryWrapper);
		if (CollUtil.isNotEmpty(menus)) {
			return menus.stream().filter(m -> StrUtil.isNotBlank(m.getPerms())).map(SysMenu::getPerms)
					.collect(Collectors.toSet());
		}
		return null;
	}

	private List<SysMenu> listUserMenu(Long userId, LambdaQueryWrapper<SysMenu> queryWrapper) {
		// 查询用户对应的权限
		LambdaQueryWrapper<SysUserRole> userRoleWrapper = Wrappers.<SysUserRole>lambdaQuery()
				.select(SysUserRole::getRoleId).eq(SysUserRole::getUserId, userId);
		List<SysUserRole> roles = sysUserRoleMapper.selectList(userRoleWrapper);
		// 查询roleId对应的menuId
		if (CollUtil.isNotEmpty(roles)) {
			List<Long> roleIds = roles.stream().map(SysUserRole::getRoleId).collect(Collectors.toList());
			LambdaQueryWrapper<SysRoleMenu> roleMenuWrapper = Wrappers.<SysRoleMenu>lambdaQuery()
					.in(SysRoleMenu::getRoleId, roleIds);
			List<SysRoleMenu> menus = sysRoleMenuMapper.selectList(roleMenuWrapper);
			if (CollUtil.isNotEmpty(menus)) {
				List<Long> menuIds = menus.stream().map(SysRoleMenu::getMenuId).collect(Collectors.toList());
				queryWrapper.in(SysMenu::getMenuId, menuIds);
				return this.baseMapper.selectList(queryWrapper);
			}
		}
		return null;
	}

	@Override
	public List<MenuLayuiDTO> listMenuLayuiTree(Long userId) {
		LambdaQueryWrapper<SysMenu> queryWrapper = Wrappers.<SysMenu>lambdaQuery().in(SysMenu::getType, SHOW_MENU_TYPE);
		boolean admin = AdminConstant.ADMIN.equals(ShiroUtils.getUser().getUsername());
		List<SysMenu> list = admin ? this.baseMapper.selectList(queryWrapper) : this.listUserMenu(userId, queryWrapper);
		// 获取根节点
		List<MenuLayuiDTO> trees = new ArrayList<>();
		if (!CollUtil.isEmpty(list)) {
			List<SysMenu> root = list.stream().filter(l -> l.getParentId().equals(0L)).collect(Collectors.toList());
			// 递归生成父子节点
			root.forEach(r -> {
				MenuLayuiDTO dto = new MenuLayuiDTO();
				dto.setMenuId(r.getMenuId());
				dto.setTitle(r.getName());
				dto.setHref(r.getUrl());
				dto.setIcon(r.getIcon());
				// 子节点
				this.genLeaf(dto, list);
				trees.add(dto);
			});
		}
		return trees;
	}

	private void genLeaf(MenuLayuiDTO root, List<SysMenu> data) {
		// 补充子节点
		List<SysMenu> rootLeaf = data.stream().filter(d -> d.getParentId().equals(root.getMenuId()))
				.collect(Collectors.toList());
		if (!CollUtil.isEmpty(rootLeaf)) {
			List<MenuLayuiDTO> trees = CollUtil.newArrayList();
			//
			rootLeaf.forEach(r -> {
				MenuLayuiDTO dto = new MenuLayuiDTO();
				dto.setMenuId(r.getMenuId());
				dto.setTitle(r.getName());
				dto.setHref(r.getUrl());
				dto.setIcon(r.getIcon());
				// 子节点
				this.genLeaf(dto, data);
				trees.add(dto);
			});
			root.setChildren(trees);
		}
	}
}
