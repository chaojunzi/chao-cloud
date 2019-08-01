package com.chao.cloud.common.extra.mybatis.generator.template;

import com.baomidou.mybatisplus.generator.config.TemplateConfig;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * rest 风格模板
 * @author 薛超
 * @since 2019年8月1日
 * @version 1.0.5
 */
@Data
@Accessors(chain = true)
public class RestTemplateConfig extends TemplateConfig {

	private String entity = "/templates/rest/entity.java";

	private String entityKt = "/templates/rest/entity.kt";

	private String service = "/templates/rest/service.java";

	private String serviceImpl = "/templates/rest/serviceImpl.java";

	private String mapper = "/templates/rest/mapper.java";

	private String xml = "/templates/rest/mapper.xml";

	private String controller = "/templates/rest/controller.java";

	public String getEntity(boolean kotlin) {
		return kotlin ? entityKt : entity;
	}

}
