package com.chao.cloud.common.web.crypto.advice;

import org.springframework.core.MethodParameter;

import com.chao.cloud.common.web.crypto.annotaion.DecryptRequest;
import com.chao.cloud.common.web.crypto.annotaion.EncryptResponse;

/**
 * 判断是否需要加解密
 * 
 * @author 薛超
 * @since 2019年12月3日
 * @version 1.0.8
 */
class NeedCrypto {

	/**
	 * 是否需要对结果加密 1.类上标注或者方法上标注,并且都为true 2.有一个标注为false就不需要加密
	 */
	static boolean needEncrypt(MethodParameter returnType) {
		boolean encrypt = false;
		boolean classPresentAnno = returnType.getContainingClass().isAnnotationPresent(EncryptResponse.class);
		boolean methodPresentAnno = returnType.getMethod().isAnnotationPresent(EncryptResponse.class);

		if (classPresentAnno) {
			// 类上标注的是否需要加密
			encrypt = returnType.getContainingClass().getAnnotation(EncryptResponse.class).value();
			// 类不加密，所有都不加密
			if (!encrypt) {
				return false;
			}
		}
		if (methodPresentAnno) {
			// 方法上标注的是否需要加密
			encrypt = returnType.getMethod().getAnnotation(EncryptResponse.class).value();
		}
		return encrypt;
	}

	/**
	 * 是否需要参数解密 1.类上标注或者方法上标注,并且都为true 2.有一个标注为false就不需要解密
	 */
	static boolean needDecrypt(MethodParameter parameter) {
		boolean encrypt = false;
		boolean classPresentAnno = parameter.getContainingClass().isAnnotationPresent(DecryptRequest.class);
		boolean methodPresentAnno = parameter.getMethod().isAnnotationPresent(DecryptRequest.class);

		if (classPresentAnno) {
			// 类上标注的是否需要解密
			encrypt = parameter.getContainingClass().getAnnotation(DecryptRequest.class).value();
			// 类不加密，所有都不加密
			if (!encrypt) {
				return false;
			}
		}
		if (methodPresentAnno) {
			// 方法上标注的是否需要解密
			encrypt = parameter.getMethod().getAnnotation(DecryptRequest.class).value();
		}
		return encrypt;
	}

}