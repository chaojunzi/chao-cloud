package com.chao.cloud.admin.system.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.chao.cloud.admin.system.domain.dto.RoleDTO;
import com.chao.cloud.admin.system.domain.dto.RoleMenuDTO;
import com.chao.cloud.admin.system.mapper.RoleMapper;
import com.chao.cloud.admin.system.mapper.RoleMenuMapper;
import com.chao.cloud.admin.system.mapper.UserMapper;
import com.chao.cloud.admin.system.mapper.UserRoleMapper;
import com.chao.cloud.admin.system.service.RoleService;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;

@Service
public class RoleServiceImpl implements RoleService {

    public static final String ROLE_ALL_KEY = "\"role_all\"";

    public static final String DEMO_CACHE_NAME = "role";

    @Autowired
    RoleMapper roleMapper;
    @Autowired
    RoleMenuMapper roleMenuMapper;
    @Autowired
    UserMapper userMapper;
    @Autowired
    UserRoleMapper userRoleMapper;

    @Override
    public List<RoleDTO> list(Map<String, Object> params) {
        List<RoleDTO> roles = roleMapper.list(params);
        return roles;
    }

    @Override
    public int count(Map<String, Object> params) {
        return roleMapper.count(params);
    }

    @Override
    public List<RoleDTO> list(Long userId, List<Long> rolesIds) {
        List<RoleDTO> roles = roleMapper.list(new HashMap<>(16));
        if (!CollUtil.isEmpty(rolesIds)) {
            roles.stream().filter(r -> rolesIds.contains(r.getRoleId())).forEach(r -> r.setRoleSign("true"));
        }
        return roles;
    }

    @Transactional
    @Override
    public int save(RoleDTO role) {
        int count = roleMapper.save(role);
        List<Long> menuIds = role.getMenuIds();
        Long roleId = role.getRoleId();
        this.batchSaveRoleMenu(roleId, menuIds);
        roleMenuMapper.removeByRoleId(roleId);
        return count;
    }

    @Transactional
    @Override
    public int remove(Long id) {
        int count = roleMapper.remove(id);
        userRoleMapper.removeByRoleId(id);
        roleMenuMapper.removeByRoleId(id);
        return count;
    }

    @Override
    public RoleDTO get(Long id) {
        RoleDTO roleDO = roleMapper.get(id);
        if (!BeanUtil.isEmpty(roleDO)) {
            List<Long> menuIds = roleMenuMapper.listMenuIdByRoleId(id);
            roleDO.setMenuIds(menuIds);
        }
        return roleDO;
    }

    @Override
    public int update(RoleDTO role) {
        int r = roleMapper.update(role);
        List<Long> menuIds = role.getMenuIds();
        Long roleId = role.getRoleId();
        roleMenuMapper.removeByRoleId(roleId);
        this.batchSaveRoleMenu(roleId, menuIds);
        return r;
    }

    @Override
    public int batchremove(Long[] ids) {
        int r = roleMapper.batchRemove(ids);
        return r;
    }

    private void batchSaveRoleMenu(Long roleId, List<Long> menuIds) {
        if (!CollUtil.isEmpty(menuIds)) {
            List<RoleMenuDTO> rms = menuIds.stream().filter(l -> ObjectUtil.isNotNull(l)).distinct().map(l -> {
                RoleMenuDTO rmDo = new RoleMenuDTO();
                rmDo.setRoleId(roleId);
                rmDo.setMenuId(l);
                return rmDo;
            }).collect(Collectors.toList());
            // 再次判断
            if (!CollUtil.isEmpty(rms)) {
                roleMenuMapper.batchSave(rms);
            }
        }
    }

}
