package com.chao.cloud.admin.system.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.support.DefaultSubjectContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.chao.cloud.admin.system.domain.dto.UserDTO;
import com.chao.cloud.admin.system.service.SessionService;
import com.chao.cloud.admin.system.shiro.ShiroUserOnline;

/**
 * 待完善
 * 
 * @author bootdo
 */
@Service
public class SessionServiceImpl implements SessionService {
    private final SessionDAO sessionDAO;

    @Autowired
    public SessionServiceImpl(SessionDAO sessionDAO) {
        this.sessionDAO = sessionDAO;
    }

    @Override
    public List<ShiroUserOnline> list() {
        List<ShiroUserOnline> list = new ArrayList<>();
        Collection<Session> sessions = sessionDAO.getActiveSessions();
        for (Session session : sessions) {
            ShiroUserOnline userOnline = new ShiroUserOnline();
            if (session.getAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY) == null) {
                continue;
            } else {
                SimplePrincipalCollection principalCollection = (SimplePrincipalCollection) session
                        .getAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY);
                UserDTO userDO = (UserDTO) principalCollection.getPrimaryPrincipal();
                userOnline.setUsername(userDO.getUsername());
            }
            userOnline.setId((String) session.getId());
            userOnline.setHost(session.getHost());
            userOnline.setStartTimestamp(session.getStartTimestamp());
            userOnline.setLastAccessTime(session.getLastAccessTime());
            userOnline.setTimeout(session.getTimeout());
            list.add(userOnline);
        }
        return list;
    }

    @Override
    public List<UserDTO> listOnlineUser() {
        List<UserDTO> list = new ArrayList<>();
        UserDTO userDO;
        Collection<Session> sessions = sessionDAO.getActiveSessions();
        for (Session session : sessions) {
            SimplePrincipalCollection principalCollection = new SimplePrincipalCollection();
            if (session.getAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY) == null) {
                continue;
            } else {
                principalCollection = (SimplePrincipalCollection) session
                        .getAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY);
                userDO = (UserDTO) principalCollection.getPrimaryPrincipal();
                list.add(userDO);
            }
        }
        return list;
    }

    @Override
    public Collection<Session> sessionList() {
        return sessionDAO.getActiveSessions();
    }

    @Override
    public boolean forceLogout(String sessionId) {
        Session session = sessionDAO.readSession(sessionId);
        session.setTimeout(0);
        return true;
    }
}
