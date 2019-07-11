package com.chao.cloud.common.extra.wx.annotation;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl;

/**
 * 微信配置支付
 * @功能：
 * @author： 薛超
 * @时间： 2019年5月27日
 * @version 1.0.0
 */
@Configuration
public class WxPaySingletonConfig {

	private static final String WX_PAY_PREFIX = "chao.cloud.wx.pay.config";

	@Bean
	public WxPayService wxPayService(WxPayConfig config) {
		WxPayServiceImpl serviceImpl = new WxPayServiceImpl();
		serviceImpl.setConfig(config);
		return serviceImpl;
	}

	@Bean
	@ConfigurationProperties(prefix = WX_PAY_PREFIX)
	public WxPayConfig wxPayConfig() {
		return new WxPayConfig();
	}

}