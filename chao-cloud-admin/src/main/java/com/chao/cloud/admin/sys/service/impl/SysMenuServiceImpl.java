package com.chao.cloud.admin.sys.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chao.cloud.admin.sys.constant.AdminConstant;
import com.chao.cloud.admin.sys.dal.entity.SysMenu;
import com.chao.cloud.admin.sys.dal.entity.SysRole;
import com.chao.cloud.admin.sys.dal.mapper.SysMenuMapper;
import com.chao.cloud.admin.sys.dal.mapper.SysRoleMapper;
import com.chao.cloud.admin.sys.domain.dto.MenuLayuiDTO;
import com.chao.cloud.admin.sys.domain.dto.UserDTO;
import com.chao.cloud.admin.sys.service.SysMenuService;
import com.chao.cloud.admin.sys.shiro.ShiroUtils;
import com.chao.cloud.common.exception.BusinessException;
import com.chao.cloud.common.extra.mybatis.generator.menu.MenuAdmin;
import com.chao.cloud.common.util.RightsUtil;

import cn.hutool.core.bean.BeanUtil;
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
	private SysRoleMapper sysRoleMapper;

	@Override
	public Set<String> listPerms(String roles) {
		LambdaQueryWrapper<SysMenu> queryWrapper = Wrappers.<SysMenu>lambdaQuery().select(SysMenu::getMenuId,
				SysMenu::getPerms);
		List<SysMenu> menus = listUserMenu(roles, queryWrapper);
		if (CollUtil.isNotEmpty(menus)) {
			Set<String> result = CollUtil.newHashSet();
			for (SysMenu m : menus) {
				String perms = m.getPerms();
				if (StrUtil.isNotBlank(perms)) {
					result.addAll(CollUtil.newHashSet(perms.split(",")));
				}
			}
			return result;
		}
		return Collections.emptySet();
	}

	@Override
	public List<MenuLayuiDTO> listMenuLayuiTree(String roles) {
		UserDTO user = ShiroUtils.getUser();
		LambdaQueryWrapper<SysMenu> queryWrapper = Wrappers.<SysMenu>lambdaQuery().in(SysMenu::getType, SHOW_MENU_TYPE);
		boolean admin = AdminConstant.ADMIN.equals(ShiroUtils.getUser().getUsername());
		List<SysMenu> list = admin ? this.baseMapper.selectList(queryWrapper)
				: this.listUserMenu(user.getRoles(), queryWrapper);
		// 获取根节点
		List<MenuLayuiDTO> trees = new ArrayList<>();
		if (!CollUtil.isEmpty(list)) {
			List<SysMenu> root = list.stream().filter(l -> l.getParentId().equals(0)).collect(Collectors.toList());
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

	/**
	 * 批量添加
	 */
	@Transactional
	@Override
	public boolean adminSaveBatch(MenuAdmin root, List<MenuAdmin> menus) {
		// 添加root
		root.setMenuId(null);// id 置空
		SysMenu menu = BeanUtil.toBean(root, SysMenu.class);
		int i = baseMapper.insert(menu);
		if (i < 1) {
			throw new BusinessException("添加菜单失败：" + root.getName());
		}
		// 批量添加
		if (CollUtil.isNotEmpty(menus)) {
			List<SysMenu> list = menus.stream().map(m -> {
				SysMenu target = BeanUtil.toBean(m, SysMenu.class);
				target.setMenuId(null);
				target.setParentId(menu.getMenuId());
				return target;
			}).collect(Collectors.toList());
			this.saveBatch(list, list.size());
		}
		return i > 0;
	}

	/**
	 * 递归删除
	 */
	@Override
	public boolean recursionRemove(Integer menuId) {
		// 查询全部菜单
		List<SysMenu> data = this.list();
		// 获取根目录菜单
		SysMenu rootMenu = data.stream().filter(d -> menuId.equals(d.getMenuId())).findFirst().orElse(null);
		if (BeanUtil.isEmpty(rootMenu)) {
			throw new BusinessException("此菜单已被删除");
		}
		// 递归获取此id下的所有菜单id
		List<Integer> menuIds = new ArrayList<>();
		this.recursionMenu(data, rootMenu, menuIds);
		// 删除所有菜单id
		int del = this.baseMapper.deleteBatchIds(menuIds);
		return del > 0;
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

	/**
	 * 
	 * @param data 数据源
	 * @param root 根菜单
	 * @param result 返回值
	 */
	private void recursionMenu(List<SysMenu> data, SysMenu root, List<Integer> result) {
		// 获取根菜单下所有菜单
		List<SysMenu> childMenus = data.stream().filter(d -> root.getMenuId().equals(d.getParentId()))
				.collect(Collectors.toList());
		if (CollUtil.isNotEmpty(childMenus)) {
			childMenus.forEach(m -> recursionMenu(data, m, result));
		}
		result.add(root.getMenuId());
	}

	private List<SysMenu> listUserMenu(String roles, LambdaQueryWrapper<SysMenu> queryWrapper) {
		boolean check = RightsUtil.checkBigInteger(roles);
		if (check) {
			// 查询用户对应的权限
			List<SysRole> roleList = sysRoleMapper.selectList(null);
			// 查询roleId对应的menuId
			if (CollUtil.isNotEmpty(roleList)) {
				List<String> rightsList = roleList.stream().filter(r -> RightsUtil.testRights(roles, r.getRoleId()))
						.map(SysRole::getRights).collect(Collectors.toList());
				if (CollUtil.isNotEmpty(rightsList)) {
					//
					List<SysMenu> temp = CollUtil.newArrayList();
					List<SysMenu> menus = this.baseMapper.selectList(queryWrapper);
					rightsList.forEach(r -> {
						// 过滤
						temp.addAll(menus.stream().filter(m -> RightsUtil.testRights(r, m.getMenuId()))
								.collect(Collectors.toList()));
					});
					// menu 去重
					return temp.stream().distinct().collect(Collectors.toList());
				}
			}
		}
		return null;
	}

}
