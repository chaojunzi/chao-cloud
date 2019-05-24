package com.chao.cloud.admin.system.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.chao.cloud.admin.system.domain.dto.UserRoleDTO;

/**
 * 
 * @功能：
 * @author： 薛超
 * @时间：2019年3月14日
 * @version 1.0.0
 */
@Mapper
public interface UserRoleMapper {

    UserRoleDTO get(Long id);

    List<UserRoleDTO> list(Map<String, Object> map);

    int count(Map<String, Object> map);

    int save(UserRoleDTO userRole);

    int update(UserRoleDTO userRole);

    int remove(Long id);

    int batchRemove(Long[] ids);

    List<Long> listRoleId(Long userId);

    int removeByUserId(Long userId);

    int removeByRoleId(Long roleId);

    int batchSave(List<UserRoleDTO> list);

    int batchRemoveByUserId(Long[] ids);
}
