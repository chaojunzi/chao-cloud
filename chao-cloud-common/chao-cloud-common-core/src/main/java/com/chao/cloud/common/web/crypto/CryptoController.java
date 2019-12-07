package com.chao.cloud.common.web.crypto;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chao.cloud.common.entity.Response;
import com.chao.cloud.common.web.config.CryptoConfig;
import com.chao.cloud.common.web.config.CryptoConfig.CryptoKey;

import lombok.Setter;

/**
 * 解密信息
 * 
 * @author 薛超
 * @since 2019年12月7日
 * @version 1.0.8
 */
@RestController
@RequestMapping("crypto")
@Setter
public class CryptoController {

	private CryptoConfig cryptoConfig;

	@RequestMapping("/key/{type}")
	public Response<String> key(@PathVariable String type) {
		CryptoKey key = cryptoConfig.getKeys().get(type);
		if (key == null) {
			return Response.error("no config");
		}
		CryptoTypeEnum typeEnum = CryptoTypeEnum.getByType(type);
		String result = "system key error";
		switch (typeEnum) {
		case RSA:
			result = key.getPublicKey();
			break;
		case DES:
			result = key.getSecretKey();
			break;
		default:
			break;
		}
		return Response.ok(result);
	}

}
