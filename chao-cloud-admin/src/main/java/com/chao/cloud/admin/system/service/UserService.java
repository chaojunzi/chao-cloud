package com.chao.cloud.admin.system.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.chao.cloud.admin.system.domain.dto.UserDTO;
import com.chao.cloud.admin.system.domain.vo.UserVO;

@Service
public interface UserService {
    UserDTO get(Long id);

    List<UserDTO> list(Map<String, Object> map);

    int count(Map<String, Object> map);

    int save(UserDTO user);

    int update(UserDTO user);

    int remove(Long userId);

    int batchremove(Long[] userIds);

    boolean exit(Map<String, Object> params);

    Set<String> listRoles(Long userId);

    int resetPwd(UserVO userVO, UserDTO userDO) throws Exception;

    /**
     * 更新个人信息
     * @param userDO
     * @return
     */
    int updatePersonal(UserDTO userDO);

}
