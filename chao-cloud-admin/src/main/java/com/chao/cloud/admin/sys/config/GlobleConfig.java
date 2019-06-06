package com.chao.cloud.admin.sys.config;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Data;

/**
 * 
 * @功能：全局配置
 * @author： 薛超
 * @时间： 2019年6月6日
 * @version 1.0.0
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "chao.cloud.globle.config")
public class GlobleConfig implements InitializingBean {
	/**
	 * 富文本编辑器生成的文件地址
	 */
	private String richPath;

	@Override
	public void afterPropertiesSet() throws Exception {
		if (StrUtil.isBlank(richPath)) {
			this.richPath = ClassUtil.getClassPath() + "public/html/rich/";
		}

	}

}
