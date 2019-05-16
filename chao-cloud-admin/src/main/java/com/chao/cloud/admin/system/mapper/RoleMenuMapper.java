package com.chao.cloud.admin.system.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.chao.cloud.admin.system.domain.dto.RoleMenuDTO;
import com.chao.cloud.common.extra.mybatis.annotation.VersionLocker;

/**
 * 角色与菜单对应关系
 * @author chglee
 * @email 1992lcg@163.com
 * @date 2017-10-03 11:08:59
 */
@Mapper
public interface RoleMenuMapper {

    RoleMenuDTO get(Long id);
 
    List<RoleMenuDTO> list(Map<String, Object> map);

    int count(Map<String, Object> map);

    int save(RoleMenuDTO roleMenu);

    @VersionLocker(false)
    int update(RoleMenuDTO roleMenu);

    int remove(Long id);

    int batchRemove(Long[] ids);

    List<Long> listMenuIdByRoleId(Long roleId);

    int removeByRoleId(Long roleId);

    int removeByMenuId(Long menuId);

    int batchSave(List<RoleMenuDTO> list);
}
