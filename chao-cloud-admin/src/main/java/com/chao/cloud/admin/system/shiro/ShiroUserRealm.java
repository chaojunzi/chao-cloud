package com.chao.cloud.admin.system.shiro;

import java.util.HashMap;
import java.util.Map;
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

import com.chao.cloud.admin.system.constant.AdminConstant;
import com.chao.cloud.admin.system.domain.dto.UserDTO;
import com.chao.cloud.admin.system.mapper.UserMapper;
import com.chao.cloud.admin.system.service.MenuService;

public class ShiroUserRealm extends AuthorizingRealm {

	@Autowired
	private UserMapper userMapper;
	@Autowired
	private MenuService menuService;

	/**
	 * 授权
	 */
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection arg0) {
		Long userId = ShiroUtils.getUserId();
		Set<String> perms = menuService.listPerms(userId);
		SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
		info.setStringPermissions(perms);
		return info;
	}

	/**
	 * 认证
	 */
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		String username = (String) token.getPrincipal();
		Map<String, Object> map = new HashMap<>(16);
		map.put("username", username);
		String password = new String((char[]) token.getCredentials());

		// 查询用户信息
		UserDTO user = userMapper.list(map).get(0);

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
		SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(user, password, getName());
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
