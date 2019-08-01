package com.chao.cloud.common.config.auth.core;

import java.lang.reflect.Method;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Map;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import com.chao.cloud.common.base.BaseProxy;
import com.chao.cloud.common.config.auth.annotation.Permission;
import com.chao.cloud.common.util.RightsUtil;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * 权限拦截
 * @author 薛超
 * @since 2019年8月1日
 * @version 1.0.5
 */
@Aspect
@Slf4j
@Setter
public class AuthUserProxy implements BaseProxy {

	private AuthUserPerm authUserPerm;

	/**
	 * 操作拦截
	 * @param pdj {@link ProceedingJoinPoint}
	 * @return Object 
	 * @throws Throwable aop代理所抛出的异常
	 */
	@Around("@annotation(com.chao.cloud.common.config.auth.annotation.Permission)")
	public Object around(ProceedingJoinPoint pdj) throws Throwable {
		Object obj = null;
		// 权限校验
		boolean check = checkPerm(pdj);
		if (check) {
			obj = pdj.proceed();
		}
		return obj;
	}

	private boolean checkPerm(ProceedingJoinPoint pdj) {
		Method method = getMethod(pdj);
		// 获取注解
		Permission permission = method.getAnnotation(Permission.class);
		Object[] args = pdj.getArgs();
		if (ObjectUtil.isNotNull(permission)) {
			// 获取当前用户
			IAuthUser user = this.getParamFirst(IAuthUser.class, args);
			int[] perm = permission.hasPerm();
			// 当前用户的权限
			Integer userType = user.getUserType();
			Integer user_rights = getUserPerm(userType, user.getStatus());
			// 该方法具有的权限
			BigInteger rights = RightsUtil.sumRights(perm);
			log.info("[interface:perm={} ]", Arrays.toString(perm));
			log.info("[user:perm={}]", user_rights);
			// 判断用户是否被冻结
			Assert.isFalse(authUserPerm.getErrorPerm().contains(user_rights), "无效的权限码: {}", user_rights);
			// 校验
			if (rights.testBit(user_rights)) {
				return true;
			}
		}
		return true;
	}

	private int getUserPerm(Integer type, Integer status) {
		// 获取用户类型
		String typeName = authUserPerm.getTypeMap().get(type);
		// 获取用户状态
		String statusName = authUserPerm.getStatusMap().get(status);
		// 根据 用户类型 状态 获取不同 的权限
		Map<String, Integer> permMap = authUserPerm.getPermMap();
		// 判断状态
		if (permMap.containsKey(statusName)) {
			return permMap.get(statusName);
		}
		// 判断用户类型
		if (permMap.containsKey(typeName)) {
			return permMap.get(typeName);
		}
		// 组合
		String permName = StrUtil.format("{}_{}", typeName, statusName);
		if (permMap.containsKey(permName)) {
			return permMap.get(permName);
		}
		return -1;
	}

}
