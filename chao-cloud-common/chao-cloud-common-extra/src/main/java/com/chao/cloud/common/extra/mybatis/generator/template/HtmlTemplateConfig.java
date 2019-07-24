package com.chao.cloud.common.extra.mybatis.generator.template;

import com.baomidou.mybatisplus.generator.config.TemplateConfig;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 生成html 
 * @功能：
 * @author： 薛超
 * @时间：2019年5月21日
 * @version 2.0
 */
@Data
@Accessors(chain = true)
public class HtmlTemplateConfig extends TemplateConfig {

	private String entity = "/templates/html/entity.java";
	private String entityKt = "/templates/html/entity.kt";
	private String service = "/templates/html/service.java";
	private String serviceImpl = "/templates/html/serviceImpl.java";
	private String mapper = "/templates/html/mapper.java";
	private String xml = "/templates/html/mapper.xml";
	private String controller = "/templates/html/controller.java";
	// html
	private String list = "/templates/html/v_list.html";// 列表
	private String add = "/templates/html/v_add.html";// 添加
	private String edit = "/templates/html/v_edit.html";// 编辑
	// 生成后项目中的位置
	private String htmlListPath = "templates/{}/list.html";
	private String htmlAddPath = "templates/{}/add.html";
	private String htmlEditPath = "templates/{}/edit.html";

	public String getEntity(boolean kotlin) {
		return kotlin ? entityKt : entity;
	}
}
