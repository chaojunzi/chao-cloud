package com.chao.cloud.common.extra.wx.annotation;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.chao.cloud.common.extra.wx.WxAppUserInfoApi;
import com.chao.cloud.common.extra.wx.impl.WxAppUserInfoServiceImpl;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.config.WxMaConfig;
import cn.binarywang.wx.miniapp.config.impl.WxMaDefaultConfigImpl;

/**
 * 微信配置
 * 
 * @author 薛超
 * @since 2019年8月1日
 * @version 1.0.5
 */
@Configuration
public class WxMaSingletonConfig {

	private static final String WX_MA_PREFIX = "chao.cloud.wx.ma.config";

	@Bean
	public WxMaService wxMaService(WxMaConfig config) {
		WxMaServiceImpl serviceImpl = new WxMaServiceImpl();
		serviceImpl.setWxMaConfig(config);
		return serviceImpl;
	}

	@Bean
	public WxAppUserInfoApi wxAppUserInfoApi(WxMaService wxMaService) {
		WxAppUserInfoServiceImpl impl = new WxAppUserInfoServiceImpl(wxMaService);
		// 是否解码
		impl.setDecode(false);
		return impl;
	}

	@Bean
	@ConfigurationProperties(prefix = WX_MA_PREFIX)
	public WxMaConfig wxMaInMemoryConfig() {
		return new WxMaDefaultConfigImpl();
	}

}