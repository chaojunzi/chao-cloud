package com.chao.cloud.admin.sys.shiro;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import com.chao.cloud.admin.sys.domain.dto.UserDTO;

/**
 * shiro utils
 * @功能：
 * @author： 薛超
 * @时间：2019年3月14日
 * @version 1.0.0
 */
public class ShiroUtils {

	public static Subject getSubjct() {
		return SecurityUtils.getSubject();
	}

	public static UserDTO getUser() { 
		Object object = getSubjct().getPrincipal();
		return (UserDTO) object;
	}

	public static Integer getUserId() {
		return getUser().getUserId();
	}
	
	public static String getRoles() {
		return getUser().getRoles();
	}

	public static void logout() {
		getSubjct().logout();
	}

}
