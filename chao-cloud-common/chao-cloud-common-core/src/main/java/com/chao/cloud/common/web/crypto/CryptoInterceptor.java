package com.chao.cloud.common.web.crypto;

import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.core.Ordered;

import com.chao.cloud.common.web.config.CryptoConfig;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import cn.hutool.crypto.symmetric.DES;
import lombok.Data;

@Data
public class CryptoInterceptor implements MethodInterceptor, Ordered {

	private int order;

	private CryptoConfig config;

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		Object[] args = invocation.getArguments();
		Parameter[] parameters = invocation.getMethod().getParameters();
		// 参数解密
		if (ArrayUtil.isNotEmpty(parameters)) {
			for (int i = 0; i < args.length; i++) {
				// 解密处理
				this.cryptoArgs(i, parameters[i], args);
			}
		}
		return invocation.proceed();
	}

	// 第一次必须在这里处理（修改索引）
	private void cryptoArgs(int i, Parameter parameter, Object[] args) {
		Crypto crypto = AnnotationUtil.getAnnotation(parameter, Crypto.class);
		if (crypto == null) {
			return;
		}
		Class<?> classType = parameter.getType();
		if (classType == String.class) {// String
			String str = StrUtil.toString(args[i]);
			if (StrUtil.isNotBlank(str)) {
				args[i] = this.cryptoStr(str, crypto);
			}
			return;
		}
		// 解密对象属性
		this.cryptoObject(args[i], crypto);// 递归
	}

	/**
	 * 递归算法
	 * 
	 * @param obj    元对象
	 * @param crypto 解密对象
	 */
	private void cryptoObject(Object obj, Crypto crypto) {
		if (obj == null) {// back
			return;
		}
		Class<?> classType = obj.getClass();
		// 普通对象
		if (ClassUtil.isSimpleValueType(classType)) {
			return;
		}
		// 数组-----------------------------------------------------
		if (classType.isArray()) {
			Class<?> type = classType.getComponentType();
			if (ArrayUtil.isEmpty(obj)) {
				return;
			}
			if (type == String.class) {
				String[] strArray = (String[]) obj;
				for (int i = 0; i < strArray.length; i++) {
					if (StrUtil.isNotBlank(strArray[i])) {
						strArray[i] = this.cryptoStr(strArray[i], crypto);
					}
				}
				return;
			}
			// 其他对象
			Object[] objArray = (Object[]) obj;
			for (int i = 0; i < objArray.length; i++) {
				// 递归处理
				cryptoObject(objArray[i], crypto);
			}
			return;
		}
		// list-----------------------------------------------------
		if (obj instanceof List) {
			// 获取list泛型
			List<Object> list = (List<Object>) obj;
			List<Object> temp = cryptoStream(list, crypto).collect(Collectors.toList());
			list.clear();
			list.addAll(temp);
			return;
		}
		// set------------------------------------------------------
		if (obj instanceof Set) {
			// 获取set泛型
			Set<Object> set = (Set<Object>) obj;
			Set<Object> temp = cryptoStream(set, crypto).collect(Collectors.toSet());
			set.clear();
			set.addAll(temp);
			return;
		}
		// map------------------------------------------------------
		if (obj instanceof Map) {
			// 获取map泛型->只修改value
			Map<Object, Object> map = (Map<Object, Object>) obj;
			map.forEach((k, v) -> {
				if (v instanceof String) {
					map.put(k, cryptoStr(v.toString(), crypto));
					return;
				}
				cryptoObject(v, crypto);
			});
			return;
		}
		// 对象------------------------------------------------------
		Field[] fields = ReflectUtil.getFields(classType);
		if (ArrayUtil.isNotEmpty(fields)) {
			for (Field field : fields) {
				Object value = ReflectUtil.getFieldValue(obj, field);
				Crypto c = AnnotationUtil.getAnnotation(field, Crypto.class);
				if (c == null) {
					continue;
				}
				if (value instanceof String) {// String
					String str = StrUtil.toString(value);
					if (StrUtil.isNotBlank(str)) {
						ReflectUtil.setFieldValue(obj, field, this.cryptoStr(str, c));
					}
					continue;
				}
				// 解密对象属性
				this.cryptoObject(value, c);// 递归
			}
		}
	}

	private Stream<Object> cryptoStream(Collection<Object> coll, Crypto crypto) {
		return coll.stream().map(s -> {
			if (s instanceof String) {
				return cryptoStr(s.toString(), crypto);
			}
			cryptoObject(s, crypto);
			return s;
		});

	}

	// 解密字符串
	private String cryptoStr(String str, Crypto crypto) {
		CryptoTypeEnum type = crypto.value();
		String result = null;
		switch (type) {
		case RSA:
			RSA rsa = config.getCryptoService(type);
			result = rsa.decryptStr(str, KeyType.PrivateKey, Charset.forName(config.getCharset()));
			break;
		case DES:
			DES des = config.getCryptoService(type);
			result = des.decryptStr(str, Charset.forName(config.getCharset()));
			break;
		default:
			break;
		}
		return result;
	}
}