package com.chao.cloud.admin.system.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.chao.cloud.admin.system.domain.dto.UserDTO;
import com.chao.cloud.admin.system.domain.dto.UserRoleDTO;
import com.chao.cloud.admin.system.domain.vo.UserVO;
import com.chao.cloud.admin.system.mapper.UserMapper;
import com.chao.cloud.admin.system.mapper.UserRoleMapper;
import com.chao.cloud.admin.system.service.DeptService;
import com.chao.cloud.admin.system.service.UserService;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.digest.DigestUtil;

@Service
public class UserServiceImpl implements UserService {
	@Autowired
	UserMapper userMapper;
	@Autowired
	UserRoleMapper userRoleMapper;
	@Autowired
	DeptService deptService;

	@Override
	public UserDTO get(Long id) {
		List<Long> roleIds = userRoleMapper.listRoleId(id);
		UserDTO user = userMapper.get(id);
		user.setRoleIds(roleIds);
		return user;
	}

	@Override
	public List<UserDTO> list(Map<String, Object> map) {
		return userMapper.list(map);
	}

	@Override
	public int count(Map<String, Object> map) {
		return userMapper.count(map);
	}

	@Transactional
	@Override
	public int save(UserDTO user) {
		int count = userMapper.save(user);
		Long userId = user.getUserId();
		userRoleMapper.removeByUserId(userId);
		this.batchSaveUserRole(userId, user.getRoleIds());
		return count;
	}

	@Override
	@Transactional
	public int update(UserDTO user) {
		int r = userMapper.update(user);
		Long userId = user.getUserId();
		userRoleMapper.removeByUserId(userId);
		this.batchSaveUserRole(userId, user.getRoleIds());
		return r;
	}

	private void batchSaveUserRole(Long userId, List<Long> roleIds) {
		if (!CollUtil.isEmpty(roleIds)) {
			List<UserRoleDTO> list = roleIds.stream().filter(l -> ObjectUtil.isNotNull(l)).distinct().map(l -> {
				UserRoleDTO ur = new UserRoleDTO();
				ur.setUserId(userId);
				ur.setRoleId(l);
				return ur;
			}).collect(Collectors.toList());
			// 再次判断
			if (!CollUtil.isEmpty(list)) {
				userRoleMapper.batchSave(list);
			}
		}
	}

	@Override
	@Transactional
	public int remove(Long userId) {
		userRoleMapper.removeByUserId(userId);
		return userMapper.remove(userId);
	}

	@Override
	public boolean exit(Map<String, Object> params) {
		boolean exit;
		exit = userMapper.list(params).size() > 0;
		return exit;
	}

	@Override
	public Set<String> listRoles(Long userId) {
		return null;
	}

	@Override
	public int resetPwd(UserVO userVO, UserDTO userDO) throws Exception {
		if ("admin".equals(userDO.getUsername())) {
			throw new Exception("超级管理员的账号不允许直接重置！");
		}
		if (Objects.equals(DigestUtil.md5Hex(userDO.getUsername() + userVO.getPwdOld()), userDO.getPassword())) {
			String password = DigestUtil.md5Hex(userDO.getUsername() + userVO.getPwdNew());
			userDO.setPassword(password);
			return userMapper.update(userDO);
		} else {
			throw new Exception("输入的旧密码有误！");
		}
	}

	@Transactional
	@Override
	public int batchremove(Long[] userIds) {
		int count = userMapper.batchRemove(userIds);
		userRoleMapper.batchRemoveByUserId(userIds);
		return count;
	}

	@Override
	public int updatePersonal(UserDTO userDO) {
		return userMapper.update(userDO);
	}
}
