package com.chao.cloud.admin.sys.service;

import java.util.List;
import java.util.Set;

import javax.validation.constraints.NotNull;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chao.cloud.admin.sys.dal.entity.SysMenu;
import com.chao.cloud.admin.sys.domain.dto.MenuLayuiDTO;
import com.chao.cloud.common.extra.mybatis.generator.menu.MenuAdmin;

import cn.hutool.core.collection.CollUtil;

/**
 * @功能：
 * @author： 超君子
 * @时间：2019-05-28
 * @version 1.0.0
 */
public interface SysMenuService extends IService<SysMenu> {

	List<Integer> SHOW_MENU_TYPE = CollUtil.toList(0, 1);// 1/2 级菜单

	/**
	 * 获取用户权限
	 * @param userId
	 * @return
	 */
	Set<String> listPerms(String roles);

	/**
	 * 获取用户左侧菜单
	 * @param userId
	 * @return
	 */
	List<MenuLayuiDTO> listMenuLayuiTree(String roles);

	/**
	 * 递归删除
	 * @param id
	 * @return
	 */
	boolean recursionRemove(@NotNull Integer id);

	/**
	 * 批量添加
	 * @param root
	 * @param menus
	 * @return
	 */
	boolean adminSaveBatch(MenuAdmin root, List<MenuAdmin> menus);
}
