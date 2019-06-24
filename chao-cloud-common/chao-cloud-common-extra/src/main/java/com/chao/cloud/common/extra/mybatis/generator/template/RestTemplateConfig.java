package com.chao.cloud.common.extra.mybatis.generator.template;

import com.baomidou.mybatisplus.generator.config.TemplateConfig;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * rest 风格模板
 * @功能：
 * @author： 薛超
 * @时间： 2019年6月24日
 * @version 1.0.0
 */
@Data
@Accessors(chain = true)
public class RestTemplateConfig extends TemplateConfig {

	@Getter(AccessLevel.NONE)
	private String entity = "/templates/rest/entity.java";

	private String entityKt = "/templates/rest/entity.kt";

	private String service = "/templates/rest/service.java";

	private String serviceImpl = "/templates/rest/serviceImpl.java";

	private String mapper = "/templates/rest/mapper.java";

	private String xml = "/templates/rest/mapper.xml";

	private String controller = "/templates/rest/controller.java";

}
