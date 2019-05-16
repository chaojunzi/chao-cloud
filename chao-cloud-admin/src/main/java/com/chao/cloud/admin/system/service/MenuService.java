package com.chao.cloud.admin.system.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.chao.cloud.admin.system.domain.dto.MenuDTO;
import com.chao.cloud.admin.system.domain.dto.MenuLayuiDTO;
import com.chao.cloud.admin.system.domain.dto.TreeDTO;

@Service
public interface MenuService {

    TreeDTO<MenuDTO> getSysMenuTree(Long id);

    List<TreeDTO<MenuDTO>> listMenuTree(Long id);

    TreeDTO<MenuDTO> getTree();

    List<MenuDTO> list(Map<String, Object> params);

    int remove(Long id);

    int save(MenuDTO menu);

    int update(MenuDTO menu);

    MenuDTO get(Long id);

    Set<String> listPerms(Long userId);

    List<MenuLayuiDTO> listMenuLayuiTree(Long userId);
}
