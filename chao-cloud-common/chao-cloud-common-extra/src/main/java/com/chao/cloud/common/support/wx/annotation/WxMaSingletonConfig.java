package com.chao.cloud.common.support.wx.annotation;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.chao.cloud.common.support.wx.WxAppUserInfoApi;
import com.chao.cloud.common.support.wx.impl.WxAppUserInfoServiceImpl;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.config.WxMaInMemoryConfig;

/**
 * <p>
 * 微信配置
 * </p>
 */
@Configuration
public class WxMaSingletonConfig {

    private static final String WX_MA_PREFIX = "wx.ma.config";

    @Bean
    public WxMaService wxMaService(WxMaInMemoryConfig config) {
        WxMaServiceImpl serviceImpl = new WxMaServiceImpl();
        serviceImpl.setWxMaConfig(config);
        return serviceImpl;
    }

    /**
     * 微信用户bean
     * 
     * @param config
     * @return
     */
    @Bean
    public WxAppUserInfoApi wxAppUserInfoApi(WxMaService wxMaService) {
        WxAppUserInfoServiceImpl impl = new WxAppUserInfoServiceImpl(wxMaService);
        // 是否解码
        impl.setDecode(false);
        return impl;
    }

    @Bean
    @ConfigurationProperties(prefix = WX_MA_PREFIX)
    public WxMaInMemoryConfig wxMaInMemoryConfig() {
        return new WxMaInMemoryConfig();
    }

}