package com.chao.cloud.common.extra.wx;

import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaPhoneNumberInfo;
import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;

/**
 * 微信小程序用户信息获取
 * @author 薛超
 * @since 2019年8月1日
 * @version 1.0.5
 */
public interface WxAppUserInfoApi {
	/**
	 * 获取用户信息
	 * @param encryptedData 加密数据
	 * @param iv 加密向量
	 * @param code 小程序端获取的code
	 * @return {@link WxMaUserInfo}
	 */
	WxMaUserInfo getUserInfo(String encryptedData, String iv, String code);

	/**
	 * 获取手机号
	 * @param encryptedData 加密数据
	 * @param iv 加密向量
	 * @param code 小程序端获取的code
	 * @return {@link WxMaPhoneNumberInfo}
	 */
	WxMaPhoneNumberInfo getPhoneNoInfo(String encryptedData, String iv, String code);

	/**
	 * 根据code获取openId
	 * @param code 小程序端获取的code
	 * @return {@link WxMaJscode2SessionResult}
	 */
	WxMaJscode2SessionResult getSessionInfo(String code);
}
