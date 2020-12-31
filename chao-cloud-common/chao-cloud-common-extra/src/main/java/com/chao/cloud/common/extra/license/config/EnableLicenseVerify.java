package com.chao.cloud.common.extra.license.config;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

/**
 * license 许可证-验证
 * 
 * @author 薛超
 * @since 2020年12月31日
 * @version 1.0.9
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(LicenseVerifyConfig.class)
public @interface EnableLicenseVerify {

}
