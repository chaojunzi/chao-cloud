package com.chao.cloud.admin.sys.shiro;

import java.util.List;
import java.util.Set;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.chao.cloud.admin.sys.constant.AdminConstant;
import com.chao.cloud.admin.sys.dal.entity.SysUser;
import com.chao.cloud.admin.sys.dal.mapper.SysUserMapper;
import com.chao.cloud.admin.sys.domain.dto.UserDTO;
import com.chao.cloud.admin.sys.service.SessionService;
import com.chao.cloud.admin.sys.service.SysMenuService;

import cn.hutool.core.bean.BeanUtil;

public class ShiroUserRealm extends AuthorizingRealm {

	@Autowired
	private SessionService sessionService;
	@Autowired
	private SysUserMapper sysUserMapper;
	@Autowired
	private SysMenuService menuService;

	/**
	 * 授权 
	 */
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection arg0) {
		UserDTO user = ShiroUtils.getUser();
		Set<String> perms = menuService.listPerms(user.getRoles());
		SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
		info.setStringPermissions(perms);
		user.setPerms(perms);// 赋值给本人
		return info;
	}

	/**
	 * 认证
	 */
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		String username = (String) token.getPrincipal();
		String password = new String((char[]) token.getCredentials());
		// 查询用户信息
		LambdaQueryWrapper<SysUser> queryWrapper = Wrappers.<SysUser>lambdaQuery().eq(SysUser::getUsername, username);
		SysUser user = sysUserMapper.selectOne(queryWrapper);

		// 账号不存在
		if (user == null) {
			throw new UnknownAccountException("账号或密码不正确");
		}

		// 密码错误
		if (!password.equals(user.getPassword())) {
			throw new IncorrectCredentialsException("账号或密码不正确");
		}

		// 账号锁定
		if (user.getStatus() == 0) {
			throw new LockedAccountException("账号已被锁定,请联系管理员");
		}
		// 踢人
		List<UserDTO> sessions = sessionService.listOnlineUser();
		sessions.stream().filter(s -> s.getUsername().equals(username)).forEach(s -> s.getSession().setTimeout(0));
		// 登录成功
		SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(BeanUtil.toBean(user, UserDTO.class), password,
				getName());
		return info;
	}

	@Override
	public boolean isPermitted(PrincipalCollection principals, String permission) {
		// 如果是管理员拥有所有的访问权限
		return AdminConstant.ADMIN.equals(ShiroUtils.getUser().getUsername())
				|| super.isPermitted(principals, permission);
	}

	@Override
	public boolean hasRole(PrincipalCollection principal, String roleIdentifier) {
		// 如果是管理员拥有所有的角色权限
		return AdminConstant.ADMIN.equals(ShiroUtils.getUser().getUsername())
				|| super.hasRole(principal, roleIdentifier);
	}

}
