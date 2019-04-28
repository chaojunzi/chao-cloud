package com.chao.cloud.common.extra.wx;

import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaPhoneNumberInfo;
import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;

/**
 * 微信小程序信息获取
 * 
 * @author xuecaho
 */
public interface WxAppUserInfoApi {
    /**
     * 
     * @param encryptedData
     * @param iv
     * @param code
     * @return
     */
    WxMaUserInfo getUserInfo(String encryptedData, String iv, String code);

    /**
     * 获取手机号
     * @param encryptedData
     * @param iv
     * @param code
     * @return
     */
    WxMaPhoneNumberInfo getPhoneNoInfo(String encryptedData, String iv, String code);

    /**
     * 根据code获取openId
     */
    WxMaJscode2SessionResult getSessionInfo(String code);
}
