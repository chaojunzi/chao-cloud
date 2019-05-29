package com.chao.cloud.admin.sys.service;

import java.util.Collection;
import java.util.List;

import org.apache.shiro.session.Session;
import org.springframework.stereotype.Service;

import com.chao.cloud.admin.sys.domain.dto.UserDTO;
import com.chao.cloud.admin.sys.shiro.ShiroUserOnline;

@Service
public interface SessionService {

	List<ShiroUserOnline> list();

	List<UserDTO> listOnlineUser();

	Collection<Session> sessionList();

	boolean forceLogout(String sessionId);
}
