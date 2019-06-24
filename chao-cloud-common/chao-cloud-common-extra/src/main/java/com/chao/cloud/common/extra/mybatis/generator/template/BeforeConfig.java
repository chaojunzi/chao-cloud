package com.chao.cloud.common.extra.mybatis.generator.template;

import com.baomidou.mybatisplus.generator.config.TemplateConfig;
import com.chao.cloud.common.exception.BusinessException;

import cn.hutool.core.lang.Singleton;
import lombok.Data;

@Data
public class BeforeConfig {
	/**
	 * 模板风格
	 */
	private String templateStyle = "html";

	/**
	 * 获取模板
	 * @return
	 */
	public TemplateConfig geTemplate() {
		Class<? extends TemplateConfig> clazz = TemplateStyle.getByName(templateStyle);
		return Singleton.get(clazz);
	}

	public enum TemplateStyle {
		/**
		 * 传统 layui+shiro 模板
		 */
		HTML(HtmlTemplateConfig.class),
		/**
		 * rest 风格模板
		 */
		REST(RestTemplateConfig.class);

		private Class<? extends TemplateConfig> clazz;

		TemplateStyle(Class<? extends TemplateConfig> clazz) {
			this.clazz = clazz;
		}

		public static Class<? extends TemplateConfig> getByName(String name) {
			TemplateStyle[] styles = TemplateStyle.values();
			for (TemplateStyle ts : styles) {
				if (ts.name().equalsIgnoreCase(name)) {
					return ts.clazz;
				}
			}
			throw new BusinessException("无效的模板名称:" + name);
		}

	}

}
