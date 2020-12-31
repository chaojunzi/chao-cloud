package com.chao.cloud.common.extra.license.config;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.chao.cloud.common.extra.license.verify.LicenseVerify;
import com.chao.cloud.common.extra.license.verify.LicenseVerifyParam;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.extra.spring.SpringUtil;
import de.schlichtherle.license.LicenseManager;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * 许可证校验配置
 * 
 * @author 薛超
 * @since 2020年12月15日
 * @version 1.0.0
 */
@Slf4j
@Data
@EqualsAndHashCode(callSuper = false)
@Configuration
@ConfigurationProperties("license.verify")
public class LicenseVerifyConfig extends LicenseVerifyParam implements BeanPostProcessor, LicenseInit {

	private boolean disable = false;
	/**
	 * 检查的时间；毫秒数
	 */
	private long checkTime = 24 * 60 * 60 * 1000L;// 1天

	@Setter(value = lombok.AccessLevel.NONE)
	@Getter(value = lombok.AccessLevel.NONE)
	private LicenseManager manager;

	@PostConstruct
	public void verify() throws Exception {
		if (disable) {//
			log.info("++++++++ 绕过许可证校验 ++++++++");
			return;
		}
		// 修改签名算法
		setLicenseNotaryAlgorithm(getAlgorithm());
		// 安装证书
		log.info("++++++++ 开始安装证书 ++++++++");
		LicenseVerify verify = new LicenseVerify();
		manager = verify.install(this);
		log.info("++++++++ 证书安装结束 ++++++++");
	}

	@Bean
	@ConditionalOnMissingBean
	public SpringUtil springUtil() {
		return new SpringUtil();
	}

	/**
	 * 证书校验线程
	 * 
	 * @param config {@link LicenseVerifyConfig}
	 * @return {@link CommandLineRunner}
	 */
	@Bean("licenseVerifyRunner")
	public CommandLineRunner licenseVerifyRunner(LicenseVerifyConfig config) {
		if (config.disable) {//
			return null;
		}
		return (String... args) -> {
			new Thread(() -> {
				while (true) {
					boolean v = LicenseVerify.verify(manager);
					if (v) {
						ThreadUtil.safeSleep(config.checkTime);
						continue;
					}
					// 关闭服务
					log.error("您的证书无效，请核查服务器是否取得授权或重新申请证书！");
					this.shutdown();
					break;
				}
			}).start();
		};
	}

	private void shutdown() {// 关闭服务
		ApplicationContext context = SpringUtil.getApplicationContext();
		ConfigurableApplicationContext ctx = (ConfigurableApplicationContext) context;
		ctx.close();
		// int exitCode = SpringApplication.exit(context, (ExitCodeGenerator) () -> 0);
		// System.exit(exitCode);
	}

}
