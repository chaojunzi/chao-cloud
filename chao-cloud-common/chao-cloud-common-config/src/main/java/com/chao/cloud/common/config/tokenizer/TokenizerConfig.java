package com.chao.cloud.common.config.tokenizer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.hankcs.hanlp.dictionary.CustomDictionary;

import cn.hutool.extra.tokenizer.TokenizerEngine;
import cn.hutool.extra.tokenizer.TokenizerUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class TokenizerConfig {

    @Value("${chao.cloud.tokenizer.word:我想要,我要,我要找}")
    private String tokenizerWord;

    @Bean
    public TokenizerEngine tokenizerEngine() {
        TokenizerEngine engine = TokenizerUtil.createEngine();
        String[] words = tokenizerWord.split(",");
        for (String word : words) {
            // 添加词库
            boolean add = CustomDictionary.add(word);
            log.info("[分词-自定义词库===> word={},result={}]", word, add);
        }
        return engine;
    }
}
