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
import cn.hutool.core.bean.BeanUtil;
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

	/**
	 * 第一次必须在这里处理（修改索引）
	 * 
	 * @param i         索引
	 * @param parameter 参数
	 * @param args      参数列表
	 */
	public void cryptoArgs(int i, Parameter parameter, Object[] args) {
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
	 * 解密对象-递归算法
	 * 
	 * @param obj    元对象
	 * @param crypto 解密对象
	 */
	public void cryptoObject(Object obj, Crypto crypto) {
		if (obj == null) {// back
			return;
		}
		Class<?> classType = obj.getClass();
		// 普通----------------------------------------------------
		if (ClassUtil.isSimpleValueType(classType)) {
			return;
		}
		// 数组-----------------------------------------------------
		if (classType.isArray()) {
			this.cryptoArray(obj, classType, crypto);
			return;
		}
		// 集合-----------------------------------------------------
		if (obj instanceof Collection) {
			this.cryptoColl(obj, crypto);
			return;
		}
		// map------------------------------------------------------
		if (obj instanceof Map) {
			this.cryptoMap(obj, crypto);
			return;
		}
		// Bean------------------------------------------------------
		if (BeanUtil.isBean(classType)) {
			this.cryptoBean(obj, classType, crypto);
			return;
		}
		// 其他------------------------------------------------------
		this.cryptoOthers(obj, crypto);
	}

	/**
	 * 解密数组类型对象
	 * 
	 * @param obj       加密对象
	 * @param classType 对象类型
	 * @param crypto    解密类型
	 */
	protected void cryptoArray(Object obj, Class<?> classType, Crypto crypto) {
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
			this.cryptoObject(objArray[i], crypto);
		}
	}

	/**
	 * 解密集合类型对象
	 * 
	 * @param obj    加密对象
	 * @param crypto 解密类型
	 */
	protected void cryptoColl(Object obj, Crypto crypto) {
		boolean isSet = obj instanceof Set;
		boolean isList = obj instanceof List;
		if (!isList && !isSet) {
			this.cryptoOthers(obj, crypto);// 用以判断其他类型
			return;
		}
		Collection<Object> coll = (Collection<Object>) obj;
		Stream<Object> stream = coll.stream().map(s -> {
			if (s instanceof String) {
				return cryptoStr(s.toString(), crypto);
			}
			this.cryptoObject(s, crypto);
			return s;
		});
		coll.clear();
		coll.addAll(isList //
				? stream.collect(Collectors.toList())//
				: stream.collect(Collectors.toSet()));
	}

	/**
	 * 解密Map类型对象
	 * 
	 * @param obj    加密对象
	 * @param crypto 解密类型
	 */
	protected void cryptoMap(Object obj, Crypto crypto) {
		// 获取map泛型->只修改value
		Map<Object, Object> map = (Map<Object, Object>) obj;
		map.forEach((k, v) -> {
			if (v instanceof String) {
				map.put(k, cryptoStr(v.toString(), crypto));
				return;
			}
			cryptoObject(v, crypto);
		});
	}

	/**
	 * 解密实体类
	 * 
	 * @param obj       加密对象
	 * @param classType 对象类型
	 * @param crypto    解密类型
	 */
	private void cryptoBean(Object obj, Class<?> classType, Crypto crypto) {
		Field[] fields = ReflectUtil.getFields(classType);
		if (ArrayUtil.isEmpty(fields)) {
			return;
		}
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

	/**
	 * 解密其他类型对象
	 * 
	 * @param obj    加密对象
	 * @param crypto 解密类型
	 */
	protected void cryptoOthers(Object obj, Crypto crypto) {
		// 请自行发挥
	}

	/**
	 * 解密字符串
	 * 
	 * @param str    加密的字符串
	 * @param crypto 解密类型
	 * @return 解密后的字符串
	 */
	protected String cryptoStr(String str, Crypto crypto) {
		CryptoTypeEnum type = crypto.value();
		switch (type) {
		case RSA:
			RSA rsa = config.getCryptoService(type);
			str = rsa.decryptStr(str, KeyType.PrivateKey, Charset.forName(config.getCharset()));
			break;
		case DES:
			DES des = config.getCryptoService(type);
			str = des.decryptStr(str, Charset.forName(config.getCharset()));
			break;
		default:
			str = this.cryptoStrExtra(str, crypto);
			break;
		}
		return str;
	}

	/**
	 * 解密字符串-自行拓展
	 * 
	 * @param str    加密的字符串
	 * @param crypto 解密类型
	 * @return 解密后的字符串
	 */
	protected String cryptoStrExtra(String str, Crypto crypto) {
		return str;
	}

}