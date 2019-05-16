package com.chao.cloud.admin.system.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.chao.cloud.admin.system.domain.dto.MenuDTO;
import com.chao.cloud.admin.system.domain.dto.MenuLayuiDTO;
import com.chao.cloud.admin.system.domain.dto.TreeDTO;
import com.chao.cloud.admin.system.mapper.MenuMapper;
import com.chao.cloud.admin.system.mapper.RoleMenuMapper;
import com.chao.cloud.admin.system.service.MenuService;
import com.chao.cloud.admin.system.utils.BuildTree;

import cn.hutool.core.collection.CollUtil;

@Service
public class MenuServiceImpl implements MenuService {
    @Autowired
    MenuMapper menuMapper;
    @Autowired
    RoleMenuMapper roleMenuMapper;

    /**
     * @param
     * @return 树形菜单
     */
    @Override
    public TreeDTO<MenuDTO> getSysMenuTree(Long id) {
        List<TreeDTO<MenuDTO>> trees = new ArrayList<TreeDTO<MenuDTO>>();
        List<MenuDTO> menuDOs = menuMapper.listMenuByUserId(id);
        for (MenuDTO sysMenuDO : menuDOs) {
            TreeDTO<MenuDTO> tree = new TreeDTO<MenuDTO>();
            tree.setId(sysMenuDO.getMenuId().toString());
            tree.setParentId(sysMenuDO.getParentId().toString());
            tree.setText(sysMenuDO.getName());
            Map<String, Object> attributes = new HashMap<>(16);
            attributes.put("url", sysMenuDO.getUrl());
            attributes.put("icon", sysMenuDO.getIcon());
            tree.setAttributes(attributes);
            trees.add(tree);
        }
        // 默认顶级菜单为０，根据数据库实际情况调整
        TreeDTO<MenuDTO> t = BuildTree.build(trees);
        return t;
    }

    @Override
    public List<MenuDTO> list(Map<String, Object> params) {
        List<MenuDTO> menus = menuMapper.list(params);
        return menus;
    }

    @Override
    public int remove(Long id) {
        int result = menuMapper.remove(id);
        return result;
    }

    @Override
    public int save(MenuDTO menu) {
        int r = menuMapper.save(menu);
        return r;
    }

    @Override
    public int update(MenuDTO menu) {
        int r = menuMapper.update(menu);
        return r;
    }

    @Override
    public MenuDTO get(Long id) {
        MenuDTO menuDO = menuMapper.get(id);
        return menuDO;
    }

    @Override
    public TreeDTO<MenuDTO> getTree() {
        List<TreeDTO<MenuDTO>> trees = new ArrayList<TreeDTO<MenuDTO>>();
        List<MenuDTO> menuDOs = menuMapper.list(new HashMap<>(16));
        for (MenuDTO sysMenuDO : menuDOs) {
            TreeDTO<MenuDTO> tree = new TreeDTO<MenuDTO>();
            tree.setId(sysMenuDO.getMenuId().toString());
            tree.setParentId(sysMenuDO.getParentId().toString());
            tree.setText(sysMenuDO.getName());
            trees.add(tree);
        }
        // 默认顶级菜单为０，根据数据库实际情况调整
        TreeDTO<MenuDTO> t = BuildTree.build(trees);
        return t;
    }

    @Override
    public Set<String> listPerms(Long userId) {
        List<String> perms = menuMapper.listUserPerms(userId);
        Set<String> permsSet = new HashSet<>();
        for (String perm : perms) {
            if (StringUtils.isNotBlank(perm)) {
                permsSet.addAll(Arrays.asList(perm.trim().split(",")));
            }
        }
        return permsSet;
    }

    @Override
    public List<TreeDTO<MenuDTO>> listMenuTree(Long id) {
        List<TreeDTO<MenuDTO>> trees = new ArrayList<TreeDTO<MenuDTO>>();
        List<MenuDTO> menuDOs = menuMapper.listMenuByUserId(id);
        for (MenuDTO sysMenuDO : menuDOs) {
            TreeDTO<MenuDTO> tree = new TreeDTO<MenuDTO>();
            tree.setId(sysMenuDO.getMenuId().toString());
            tree.setParentId(sysMenuDO.getParentId().toString());
            tree.setText(sysMenuDO.getName());
            Map<String, Object> attributes = new HashMap<>(16);
            attributes.put("url", sysMenuDO.getUrl());
            attributes.put("icon", sysMenuDO.getIcon());
            tree.setAttributes(attributes);
            trees.add(tree);
        }
        // 默认顶级菜单为０，根据数据库实际情况调整
        List<TreeDTO<MenuDTO>> list = BuildTree.buildList(trees, "0");
        return list;
    }

    @Override
    public List<MenuLayuiDTO> listMenuLayuiTree(Long userId) {
        List<MenuLayuiDTO> trees = new ArrayList<>();
        List<MenuDTO> list = menuMapper.listMenuByUserId(userId);
        // 获取根节点
        if (!CollUtil.isEmpty(list)) {
            List<MenuDTO> root = list.stream().filter(l -> l.getParentId().equals(0L)).collect(Collectors.toList());
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

    private void genLeaf(MenuLayuiDTO root, List<MenuDTO> data) {
        // 补充子节点
        List<MenuDTO> rootLeaf = data.stream().filter(d -> d.getParentId().equals(root.getMenuId()))
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
