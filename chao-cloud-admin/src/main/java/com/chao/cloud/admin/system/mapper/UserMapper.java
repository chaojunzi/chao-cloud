package com.chao.cloud.admin.system.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chao.cloud.admin.system.domain.dto.UserDTO;

/**
 * 
 * @功能：
 * @author： 薛超
 * @时间： 2019年5月27日
 * @version 1.0.0
 */
@Mapper
public interface UserMapper extends BaseMapper<UserDTO> {

	UserDTO get(Long userId);

	List<UserDTO> list(Map<String, Object> map);

	int count(Map<String, Object> map);

	int save(UserDTO user);

	int update(UserDTO user);

	int remove(Long user_id);

	int batchRemove(Long[] userIds);

}
