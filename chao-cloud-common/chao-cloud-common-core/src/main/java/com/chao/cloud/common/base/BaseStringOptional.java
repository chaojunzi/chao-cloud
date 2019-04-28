package com.chao.cloud.common.base;

import org.apache.commons.lang3.StringUtils;

public interface BaseStringOptional {
    // 斜杠
    String VIRGULE = "/";

   public static String getFullHeadImgUrl(String imgDomain, String headImg) {
        if (StringUtils.isNotBlank(headImg)) {
            if (!headImg.contains("http")) {
                // 非斜杠
                String virgule = "";
                if (!headImg.startsWith(VIRGULE)) {
                    virgule = VIRGULE;
                }
                headImg = imgDomain + virgule + headImg;
            }
        }
        return headImg;
    }

}
