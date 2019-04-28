package com.chao.cloud.common.support.emoji.annotation;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.chao.cloud.common.support.emoji.proxy.EmojiFilterProxy;

@Configuration
public class EmojiFilterConfiguration {

    @Bean
    public EmojiFilterProxy emojiFilterProxy() {
        return new EmojiFilterProxy();
    }

}
