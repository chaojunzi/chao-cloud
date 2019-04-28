package com.chao.cloud.common.support.wx.impl;

import com.chao.cloud.common.base.BaseLogger;
import com.chao.cloud.common.exception.BusinessException;
import com.chao.cloud.common.support.wx.WxAppUserInfoApi;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.WxMaUserService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaPhoneNumberInfo;
import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxError;
import me.chanjar.weixin.common.error.WxErrorException;

/**
 * 微信小程序信息获取
 * 
 * @author xuecaho
 */
@Slf4j
public class WxAppUserInfoServiceImpl implements WxAppUserInfoApi, BaseLogger {

    private boolean isDecode = false;

    public WxAppUserInfoServiceImpl(WxMaService wxMaService) {
        this.wxMaUserService = wxMaService.getUserService();
    }

    private WxMaUserService wxMaUserService;

    @Override
    public WxMaUserInfo getUserInfo(String encryptedData, String iv, String code) {
        try {
            // 获取session_key
            log.info("[encryptedData={}]", encryptedData);
            log.info("[iv={}]", iv);
            WxMaJscode2SessionResult sessionInfo = wxMaUserService.getSessionInfo(code);
            log.info("[sessionInfo={}]", sessionInfo);
            // 获取用户信息
            return wxMaUserService.getUserInfo(sessionInfo.getSessionKey(), getEncryptedData(encryptedData), iv);
        } catch (WxErrorException e) {
            WxError error = e.getError();
            throw new BusinessException(error.getErrorMsg());
        }
    }

    @Override
    public WxMaPhoneNumberInfo getPhoneNoInfo(String encryptedData, String iv, String code) {
        WxMaJscode2SessionResult sessionInfo = null;
        try {
            // 获取session_key
            sessionInfo = wxMaUserService.getSessionInfo(code);
            log.info("【获取手机号】[SessionKey={}]", sessionInfo.getSessionKey());
            log.info("【获取手机号】[iv={}]", iv);
            log.info("【获取手机号】[encryptedData={}]", encryptedData);
            // 获取用户信息
            return wxMaUserService.getPhoneNoInfo(sessionInfo.getSessionKey(), getEncryptedData(encryptedData), iv);
        } catch (WxErrorException e) {
            log.error("【获取手机号失败】[sessionInfo={}]", sessionInfo);
            log.error("【获取手机号失败】[iv={}]", iv);
            log.error("【获取手机号失败】[encryptedData={}]", encryptedData);
            WxError error = e.getError();
            throw new BusinessException(error.getErrorMsg());
        }
    }

    @Override
    public WxMaJscode2SessionResult getSessionInfo(String code) {
        try {
            // 获取session_key
            WxMaJscode2SessionResult sessionInfo = wxMaUserService.getSessionInfo(code);
            // 获取用户信息
            return sessionInfo;
        } catch (WxErrorException e) {
            WxError error = e.getError();
            throw new BusinessException(error.getErrorMsg());
        }
    }

    private String getEncryptedData(String encodeData) {
        // 是否解码-默认为否
        if (!isDecode) {
            return encodeData;
        }
        try {
            return java.net.URLDecoder.decode(encodeData, "UTF-8");
        } catch (Exception e) {
            log.error("解码失败:\\nEncryptedData{},\\nError:{}", encodeData, e.getMessage());
            throw new RuntimeException("解码失败:EncryptedData");
        }
    }

    public void setDecode(boolean isDecode) {
        this.isDecode = isDecode;
    }
}
