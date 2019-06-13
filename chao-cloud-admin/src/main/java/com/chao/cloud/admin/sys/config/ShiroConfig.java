package com.chao.cloud.admin.sys.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;

import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.SessionListener;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.session.mgt.eis.MemorySessionDAO;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.Cookie;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.chao.cloud.admin.sys.shiro.ShiroSessionListener;
import com.chao.cloud.admin.sys.shiro.ShiroUserRealm;

import at.pollux.thymeleaf.shiro.dialect.ShiroDialect;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import net.sf.ehcache.CacheManager;

/**
 * shiro 配置
 * @功能：
 * @author： 薛超
 * @时间：2019年3月13日
 * @version 1.0.0
 */
@Configuration
@Slf4j
public class ShiroConfig {

	@Value("${server.servlet.session.timeout:1800}")
	private int tomcatTimeout;

	@Value("${chao.cloud.admin.shiro.session-id-name:SHIRO-COOKIE}")
	private String sessionIdName;

	/**
	 * ShiroDialect，为了在thymeleaf里使用shiro的标签的bean
	 *
	 * @return
	 */
	@Bean
	public ShiroDialect shiroDialect() {
		return new ShiroDialect();
	}

	@Bean
	public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager) {
		ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
		shiroFilterFactoryBean.setSecurityManager(securityManager);
		shiroFilterFactoryBean.setLoginUrl("/login");
		shiroFilterFactoryBean.setSuccessUrl("/index");
		// shiroFilterFactoryBean.setUnauthorizedUrl(""); // 全局异常处理配置
		LinkedHashMap<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
		filterChainDefinitionMap.put("/login/**", "anon");
		filterChainDefinitionMap.put("/logout", "anon");
		filterChainDefinitionMap.put("/error/**", "anon");
		//
		filterChainDefinitionMap.put("/favicon.ico", "anon");
		filterChainDefinitionMap.put("/getVerify", "anon");
		//
		filterChainDefinitionMap.put("/css/**", "anon");
		filterChainDefinitionMap.put("/js/**", "anon");
		filterChainDefinitionMap.put("/img/**", "anon");
		// 获取static 静态目录
		filterChainDefinitionMap.put("/echarts/**", "anon");
		filterChainDefinitionMap.put("/layui/**", "anon");
		filterChainDefinitionMap.put("/core/**", "anon");
		//
		filterChainDefinitionMap.put("/**", "authc");
		// 获取顺序图
		log.info("[shiro->filter:{}]", JSONUtil.toJsonPrettyStr(filterChainDefinitionMap));
		shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
		return shiroFilterFactoryBean;
	}

	@Bean
	public static LifecycleBeanPostProcessor getLifecycleBeanPostProcessor() {
		return new LifecycleBeanPostProcessor();
	}

	/**
	 * 开启shiro aop注解支持.
	 * 使用代理方式;所以需要开启代码支持;
	 *
	 * @param securityManager
	 * @return
	 */
	@Bean
	public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
		AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
		authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
		return authorizationAttributeSourceAdvisor;
	}

	@Bean
	public SessionDAO sessionDAO() {
		return new MemorySessionDAO();
	}

	/**
	 * shiro session的管理
	 */
	@Bean
	public SessionManager sessionManager(SessionDAO sessionDAO) {
		DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
		sessionManager.setGlobalSessionTimeout(tomcatTimeout * 1000);
		sessionManager.setSessionDAO(sessionDAO);
		Collection<SessionListener> listeners = new ArrayList<SessionListener>();
		listeners.add(new ShiroSessionListener());
		sessionManager.setSessionListeners(listeners);
		// 重置 sessionid->防止与 tomcat 等容器冲突
		Cookie cookie = new SimpleCookie(sessionIdName);
		sessionManager.setSessionIdCookie(cookie);
		return sessionManager;
	}

	@Bean
	public ShiroUserRealm userRealm() {
		ShiroUserRealm userRealm = new ShiroUserRealm();
		return userRealm;
	}

	@Bean
	public SecurityManager securityManager(EhCacheManager ehCacheManager, ShiroUserRealm realm,
			SessionManager sessionManager) {
		DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
		// 设置realm.
		securityManager.setRealm(realm);
		securityManager.setCacheManager(ehCacheManager);
		securityManager.setSessionManager(sessionManager);
		return securityManager;
	}

	@Bean
	public EhCacheManager ehCacheManager(CacheManager cacheManager) {
		EhCacheManager em = new EhCacheManager();
		em.setCacheManager(cacheManager);
		return em;
	}

}