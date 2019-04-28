package com.chao.cloud.common.support.voice.annotation;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.chao.cloud.common.support.voice.SpeechRecognitionService;
import com.chao.cloud.common.support.voice.baidu.AipSpeechClient;
import com.chao.cloud.common.support.voice.baidu.BaiDuSpeechRecognitionService;

/**
 * 语音ai
 * @功能：
 * @author： 薛超
 * @时间：2019年3月6日
 * @version 1.0.0
 */
@Configuration
public class VoiceAIConfig {

    private final static String VOICE_CLIENT_PREFIX = "chao.cloud.ai.voice.client";

    /**
     * 语音转换文字
     * @return 
     */
    @Bean
    public SpeechRecognitionService baiDuSpeechRecognitionService(AipSpeechClient client) {
        return new BaiDuSpeechRecognitionService(client.getAipSpeech());
    }

    @Bean
    @ConfigurationProperties(prefix = VOICE_CLIENT_PREFIX)
    public AipSpeechClient aipSpeechClient() {
        return new AipSpeechClient();
    }

}
