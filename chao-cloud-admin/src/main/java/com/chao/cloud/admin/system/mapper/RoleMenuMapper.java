package com.chao.cloud.admin.system.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chao.cloud.admin.system.domain.dto.RoleMenuDTO;

/**
 * 
 * @功能：角色与菜单对应关系
 * @author： 薛超
 * @时间： 2019年5月27日
 * @version 1.0.0
 */
@Mapper
public interface RoleMenuMapper extends BaseMapper<RoleMenuDTO>{

    RoleMenuDTO get(Long id);

    List<RoleMenuDTO> list(Map<String, Object> map);

    int count(Map<String, Object> map);

    int save(RoleMenuDTO roleMenu);

    int update(RoleMenuDTO roleMenu);

    int remove(Long id);

    int batchRemove(Long[] ids);

    List<Long> listMenuIdByRoleId(Long roleId);

    int removeByRoleId(Long roleId);

    int removeByMenuId(Long menuId);

    int batchSave(List<RoleMenuDTO> list);
}
