package com.chao.cloud.admin.sys.service.impl;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chao.cloud.admin.sys.dal.entity.SysUser;
import com.chao.cloud.admin.sys.dal.entity.SysUserRole;
import com.chao.cloud.admin.sys.dal.mapper.SysUserMapper;
import com.chao.cloud.admin.sys.domain.dto.UserDTO;
import com.chao.cloud.admin.sys.domain.vo.UserVO;
import com.chao.cloud.admin.sys.service.SysUserRoleService;
import com.chao.cloud.admin.sys.service.SysUserService;
import com.chao.cloud.common.exception.BusinessException;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.digest.DigestUtil;

/**
 * @功能：
 * @author： 超君子
 * @时间：2019-05-28
 * @version 1.0.0
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

	@Autowired
	private SysUserRoleService sysUserRoleService;

	@Override
	public UserDTO get(Long userId) {
		UserDTO user = BeanUtil.toBean(this.baseMapper.selectById(userId), UserDTO.class);
		LambdaQueryWrapper<SysUserRole> queryWrapper = Wrappers.<SysUserRole>lambdaQuery();
		queryWrapper.select(SysUserRole::getRoleId).eq(SysUserRole::getUserId, userId);
		List<SysUserRole> list = sysUserRoleService.list(queryWrapper);
		if (CollUtil.isNotEmpty(list)) {
			user.setRoleIds(list.stream().map(SysUserRole::getRoleId).collect(Collectors.toList()));
		}
		return user;
	}

	@Transactional
	@Override
	public int save(UserDTO user) {
		// 添加
		SysUser sysUser = BeanUtil.toBean(user, SysUser.class);
		int count = this.baseMapper.insert(sysUser);
		Long userId = sysUser.getUserId();
		this.removeUserRoleByUserId(userId);
		this.batchSaveUserRole(userId, user.getRoleIds());
		return count;
	}

	@Override
	@Transactional
	public int update(UserDTO user) {
		int r = this.baseMapper.updateById(BeanUtil.toBean(user, SysUser.class));
		Long userId = user.getUserId();
		this.removeUserRoleByUserId(userId);
		this.batchSaveUserRole(userId, user.getRoleIds());
		return r;
	}

	@Override
	@Transactional
	public int remove(Long userId) {
		this.removeUserRoleByUserId(userId);
		return this.baseMapper.deleteById(userId);
	}

	@Override
	public int resetPwd(UserVO userVO, UserDTO user) {
		if ("admin".equals(user.getUsername())) {
			throw new BusinessException("超级管理员的账号不允许直接重置！");
		}
		if (Objects.equals(DigestUtil.md5Hex(user.getUsername() + userVO.getPwdOld()), user.getPassword())) {
			String password = DigestUtil.md5Hex(user.getUsername() + userVO.getPwdNew());
			user.setPassword(password);
			return this.baseMapper.updateById(BeanUtil.toBean(user, SysUser.class));
		} else {
			throw new BusinessException("输入的旧密码有误！");
		}
	}

	@Transactional
	@Override
	public int removeBatch(Long[] userIds) {
		int count = this.baseMapper.deleteBatchIds(CollUtil.toList(userIds));
		this.removeUserRoleByUserId(userIds);
		return count;
	}

	private void batchSaveUserRole(Long userId, List<Long> roleIds) {
		if (!CollUtil.isEmpty(roleIds)) {
			List<SysUserRole> list = roleIds.stream().filter(l -> ObjectUtil.isNotNull(l)).distinct().map(l -> {
				SysUserRole ur = new SysUserRole();
				ur.setUserId(userId);
				ur.setRoleId(l);
				return ur;
			}).collect(Collectors.toList());
			// 再次判断
			if (!CollUtil.isEmpty(list)) {
				sysUserRoleService.saveBatch(list);
			}
		}
	}

	private void removeUserRoleByUserId(Long... userIds) {
		if (ArrayUtil.isNotEmpty(userIds)) {
			LambdaQueryWrapper<SysUserRole> queryWrapper = Wrappers.<SysUserRole>lambdaQuery();
			queryWrapper.in(SysUserRole::getUserId, userIds);
			sysUserRoleService.remove(queryWrapper);
		}
	}

}
