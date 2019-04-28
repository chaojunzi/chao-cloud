package com.chao.cloud.common.support.voice.baidu;

import org.springframework.beans.factory.InitializingBean;

import lombok.Data;

@Data
public class AipSpeechClient implements InitializingBean {

    private String appId;
    private String apiKey;
    private String secretKey;
    private Integer timeoutSocket = 60000;
    private Integer timeoutConnection = 2000;

    private TopspeedAipSpeech aipSpeech;

    @Override
    public void afterPropertiesSet() throws Exception {
        aipSpeech = new TopspeedAipSpeech(appId, apiKey, secretKey);
        aipSpeech.setConnectionTimeoutInMillis(timeoutConnection);
        aipSpeech.setSocketTimeoutInMillis(timeoutSocket);
        // 可选：设置log4j日志输出格式，若不设置，则使用默认配置
        // 也可以直接通过jvm启动参数设置此环境变量
        /*
         * System.setProperty("aip.log4j.conf",
         * "classpath:log/log4j.properties");
         */
    }

}
