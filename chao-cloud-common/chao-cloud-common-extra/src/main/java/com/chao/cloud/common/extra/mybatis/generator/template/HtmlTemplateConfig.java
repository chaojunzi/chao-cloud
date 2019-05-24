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

    private String list = "/templates/v_list.html";// 列表
    private String add = "/templates/v_add.html";// 添加
    private String edit = "/templates/v_edit.html";// 编辑

    private String htmlListPath = "templates/{}/list.html";
    private String htmlAddPath = "templates/{}/add.html";
    private String htmlEditPath = "templates/{}/edit.html";

}
