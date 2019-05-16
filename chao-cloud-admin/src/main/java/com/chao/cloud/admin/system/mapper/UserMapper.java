package com.chao.cloud.admin.system.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.chao.cloud.admin.system.domain.dto.UserDTO;
import com.chao.cloud.common.extra.mybatis.annotation.VersionLocker;

/**
 * 
 * @author xuechao
 * @date 2019-05-10 15:36:01
 */
@Mapper
public interface UserMapper {

    UserDTO get(Long userId);

    List<UserDTO> list(Map<String, Object> map);

    int count(Map<String, Object> map);

    int save(UserDTO user);

    @VersionLocker(false)
    int update(UserDTO user);

    int remove(Long user_id);

    int batchRemove(Long[] userIds);

}
