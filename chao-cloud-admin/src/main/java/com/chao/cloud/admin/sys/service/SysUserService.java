package com.chao.cloud.admin.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chao.cloud.admin.sys.dal.entity.SysUser;
import com.chao.cloud.admin.sys.domain.dto.UserDTO;
import com.chao.cloud.admin.sys.domain.vo.UserVO;

/**
 * @功能：
 * @author： 超君子
 * @时间：2019-05-28
 * @version 1.0.0
 */
public interface SysUserService extends IService<SysUser> {

	UserDTO get(Long userId);

	int save(UserDTO user);

	int update(UserDTO user);

	int remove(Long userId);

	int removeBatch(Long[] userIds);

	int resetPwd(UserVO userVO, UserDTO user);
}
