package com.chao.cloud.common.support.map.annotation;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.chao.cloud.common.support.map.MapService;
import com.chao.cloud.common.support.map.tencent.TencentMapService;

/**
 * 地图功能
 * @功能：
 * @author： 薛超
 * @时间：2019年3月6日
 * @version 1.0.0
 */
@Configuration
public class MapConfig { 

    @Value("${chao.cloud.map.tencent.key}")
    private String key;

    /**
     * 腾讯地图服务
     * @return
     */
    @Bean
    public MapService mapService() {
        return new TencentMapService(key);
    }

}
