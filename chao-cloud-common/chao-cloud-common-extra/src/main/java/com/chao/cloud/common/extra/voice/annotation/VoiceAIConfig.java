package com.chao.cloud.common.extra.voice.annotation;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.chao.cloud.common.extra.voice.SpeechRecognitionService;
import com.chao.cloud.common.extra.voice.baidu.AipSpeechClient;
import com.chao.cloud.common.extra.voice.baidu.BaiDuSpeechRecognitionService;

/**
 * 语音ai
 * @author 薛超
 * @since 2019年8月1日
 * @version 1.0.5
 */
@Configuration
public class VoiceAIConfig {

	private final static String VOICE_CLIENT_PREFIX = "chao.cloud.ai.voice.client";

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
