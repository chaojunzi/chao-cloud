package com.chao.cloud.common.extra.feign.encoder;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.web.multipart.MultipartFile;

import cn.hutool.core.util.ArrayUtil;
import feign.RequestTemplate;
import feign.codec.EncodeException;
import feign.codec.Encoder;
import feign.form.ContentType;
import feign.form.FormEncoder;
import feign.form.MultipartFormContentProcessor;
import feign.form.spring.SpringManyMultipartFilesWriter;
import feign.form.spring.SpringSingleMultipartFileWriter;

/**
 * feign-文件与表单参数处理
 * @author 薛超
 * @since 2019年9月18日
 * @version 1.0.7
 */
public class FeignSpringFormEncoder extends FormEncoder {
	public FeignSpringFormEncoder() {
		this(new Default());
	}

	public FeignSpringFormEncoder(Encoder delegate) {
		super(delegate);
		MultipartFormContentProcessor processor = (MultipartFormContentProcessor) this
				.getContentProcessor(ContentType.MULTIPART);
		processor.addFirstWriter(new SpringSingleMultipartFileWriter());
		processor.addFirstWriter(new SpringManyMultipartFilesWriter());
	}

	public void encode(Object object, Type bodyType, RequestTemplate template) throws EncodeException {
		// MultipartFile 单文件
		if (bodyType.equals(MultipartFile.class)) {
			MultipartFile file = (MultipartFile) object;
			Map<String, Object> data = Collections.singletonMap(file.getName(), object);
			super.encode(data, MAP_STRING_WILDCARD, template);
			return;
		}
		// MultipartFile 文件数组
		if (bodyType.equals(MultipartFile[].class)) {
			MultipartFile[] files = (MultipartFile[]) object;
			if (ArrayUtil.isNotEmpty(files)) {
				// 获取第一个文件的名字
				Map<String, Object> data = Collections.singletonMap(files[0].getName(), object);
				super.encode(data, MAP_STRING_WILDCARD, template);
				return;
			}
		}
		// map对象-如：表单
		if (object instanceof Map) {
			// 获取对象中的multpart
			@SuppressWarnings("unchecked")
			Map<String, Object> data = (Map<String, Object>) object;
			Set<String> nullSet = new HashSet<>();
			for (Map.Entry<String, Object> entry : data.entrySet()) {
				if (entry.getValue() == null) {
					nullSet.add(entry.getKey());
				}
			}
			for (String s : nullSet) {
				data.remove(s);
			}
			super.encode(data, MAP_STRING_WILDCARD, template);
			return;
		}
		//
		super.encode(object, bodyType, template);
	}

}