package com.chao.cloud.common.convert;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.MappingJacksonValue;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JsonHttpMessageConverter extends FastJsonHttpMessageConverter {

    private SerializerFeature[] features;
    private final int maxLength = 900;

    public JsonHttpMessageConverter() {
        super();
        this.features = new SerializerFeature[] { SerializerFeature.WriteDateUseDateFormat,
                SerializerFeature.DisableCircularReferenceDetect, SerializerFeature.QuoteFieldNames };

        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        List<MediaType> fastMediaTypes = new ArrayList<MediaType>();
        // 处理中文乱码问题1
        // 处理中文乱码问题2
        fastJsonConfig.setCharset(CharsetUtil.CHARSET_UTF_8);
        fastMediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
        this.setSupportedMediaTypes(fastMediaTypes);
    }

    @Override
    protected void writeInternal(Object obj, HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException {
        // jsonp格式
        try (OutputStream out = outputMessage.getBody();) {
            String jsonText = null;
            // jsonp 跨域
            if (obj instanceof MappingJacksonValue) {
                MappingJacksonValue jsonpObject = (MappingJacksonValue) obj;
                String text = JSON.toJSONString(jsonpObject.getValue(), this.features);
                jsonText = new StringBuilder(jsonpObject.getJsonpFunction()).append("(").append(text).append(")")
                        .toString();
            } else {
                // 普通返回
                jsonText = JSON.toJSONString(obj, SerializerFeature.WriteDateUseDateFormat);
            }
            log.info("【返回值】----> \n{}", StrUtil.brief(jsonText, maxLength));
            byte[] bytes = StrUtil.utf8Bytes(jsonText);
            out.write(bytes);
        } catch (Exception e) {
            log.error("[返回值异常：obj={},error={}]", obj, e);
        }

    }
}