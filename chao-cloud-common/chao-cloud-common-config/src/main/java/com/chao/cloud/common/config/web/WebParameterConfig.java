package com.chao.cloud.common.config.web;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import com.chao.cloud.common.annotation.ArgumentAnnotation;
import com.chao.cloud.common.convert.JsonHttpMessageConverter;
import com.chao.cloud.common.core.ApplicationOperation;

import cn.hutool.system.OsInfo;
import cn.hutool.system.SystemUtil;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class WebParameterConfig extends WebMvcConfigurationSupport {

    /**
     * ResourceProperties
     */
    private static final String[] CLASSPATH_RESOURCE_LOCATIONS = { "classpath:/META-INF/resources/",
            "classpath:/resources/", "classpath:/static/", "classpath:/public/" };
    /**
     * 资源文件路径
     */
    @Value("${spring.resources.static-locations:classpath:/static/,classpath:/public/,classpath:/META-INF/resources/,classpath:/resources/}")
    private String staticLocation;

    @Override
    protected void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        // 获取自定义的参数解析器
        List<HandlerMethodArgumentResolver> list = ApplicationOperation
                .getInterfaceImplClass(HandlerMethodArgumentResolver.class);
        List<HandlerMethodArgumentResolver> arguments = list.stream()
                .filter(l -> l.getClass().isAnnotationPresent(ArgumentAnnotation.class)).collect(Collectors.toList());
        argumentResolvers.addAll(arguments);
        super.addArgumentResolvers(argumentResolvers);
    }

    @Override
    protected void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new JsonHttpMessageConverter());
        super.configureMessageConverters(converters);

    }

    /**
     * 添加文件映射地址
     */
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        String[] staticLocations = staticLocation.split(",");
        OsInfo osInfo = SystemUtil.getOsInfo();
        log.info("\n当前系统--->:\n{}", osInfo);
        if (osInfo.isWindows()) {
            log.info("系统不支持 Windows 为生产环境");
            staticLocations = CLASSPATH_RESOURCE_LOCATIONS;
        }
        log.info("staticLocations={}", Arrays.toString(staticLocations));
        registry.addResourceHandler("/**").addResourceLocations(staticLocations);
        super.addResourceHandlers(registry);
    }

    /**
     * 跨域访问
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowCredentials(true).allowedMethods("GET", "POST");
    }

}
