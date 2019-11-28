package com.chao.cloud.common.extra.emoji.annotation;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.chao.cloud.common.extra.emoji.proxy.EmojiFilterProxy;

@Configuration
public class EmojiFilterConfig {

    @Bean
    public EmojiFilterProxy emojiFilterProxy() {
        return new EmojiFilterProxy();
    }

}
