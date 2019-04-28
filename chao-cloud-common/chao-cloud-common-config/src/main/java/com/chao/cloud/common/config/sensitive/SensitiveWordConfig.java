package com.chao.cloud.common.config.sensitive;

import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.dfa.SensitiveUtil;

@Configuration
public class SensitiveWordConfig implements InitializingBean {

    public static String filePath = "config/SensitiveWord.txt"; // 默认路径

    @Override
    public void afterPropertiesSet() throws Exception {
        List<String> list = new LinkedList<>();
        try (InputStream streamSafe = ResourceUtil.getStreamSafe(filePath);) {
            IoUtil.readUtf8Lines(streamSafe, list);
        } catch (Exception e) {
            throw e;
        }
        // 异步初始化
        SensitiveUtil.init(list, true);
    }

}
