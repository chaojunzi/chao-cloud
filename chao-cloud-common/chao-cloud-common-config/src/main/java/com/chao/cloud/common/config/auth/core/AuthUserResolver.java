package com.chao.cloud.common.config.auth.core;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.chao.cloud.common.annotation.ArgumentAnnotation;
import com.chao.cloud.common.config.auth.annotation.ResolverEnum;
import com.chao.cloud.common.config.redis.IRedisService;

import cn.hutool.core.lang.Assert;
import lombok.Setter;

/**
 * 解析数据
 * @author 薛超
 * @since 2019年8月1日
 * @version 1.0.5
 */
@ArgumentAnnotation
@Setter
public class AuthUserResolver implements HandlerMethodArgumentResolver {

	private IRedisService redisService;

	private Integer type;

	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.getParameterType().equals(IAuthUser.class);
	}

	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
		// 获取登录用户
		String token = getToken(webRequest);
		Assert.notBlank(token, "无效的登录凭证");
		//
		IAuthUser user = this.getUser(token);
		// 用户登录
		Assert.notNull(user, "请进行登录");
		return user;
	}

	private IAuthUser getUser(String token) {
		// 获取用户
		String json = redisService.get(IAuthUser.USER_LOGIN_TOKEN + token);
		return IAuthUser.parseAuthUser(json);
	}

	/**
	 *  登录凭证
	 * @param webRequest  {@link NativeWebRequest}
	 * @return token
	 */
	private String getToken(NativeWebRequest webRequest) {
		ResolverEnum byType = ResolverEnum.getByType(type);
		switch (byType) {
		case HEADER:
			// 获取凭证
			return webRequest.getHeader(IAuthUser.REQUEST_PARAM_TOKEN);
		case PARAM:
			return webRequest.getParameter(IAuthUser.REQUEST_PARAM_TOKEN);
		default:
			break;
		}
		return null;
	}

}