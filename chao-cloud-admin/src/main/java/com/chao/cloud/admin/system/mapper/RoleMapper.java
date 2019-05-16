package com.chao.cloud.admin.system.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.chao.cloud.admin.system.domain.dto.RoleDTO;
import com.chao.cloud.common.extra.mybatis.annotation.VersionLocker;

/**
 * 角色
 * @功能：
 * @author： 薛超
 * @时间：2019年3月14日
 * @version 1.0.0
 */
@Mapper
public interface RoleMapper {

    RoleDTO get(Long roleId);

    List<RoleDTO> list(Map<String, Object> map);

    int count(Map<String, Object> map);

    int save(RoleDTO role);

    @VersionLocker(false)
    int update(RoleDTO role);

    int remove(Long roleId);

    int batchRemove(Long[] roleIds);
}
